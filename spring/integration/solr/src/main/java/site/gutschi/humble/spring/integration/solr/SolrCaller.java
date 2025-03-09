package site.gutschi.humble.spring.integration.solr;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.impl.HttpJdkSolrClient;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.client.solrj.util.ClientUtils;
import org.apache.solr.common.params.CommonParams;
import org.apache.solr.common.params.DisMaxParams;
import org.springframework.stereotype.Service;
import site.gutschi.humble.spring.tasks.model.Task;
import site.gutschi.humble.spring.tasks.ports.SearchCaller;

import java.util.Arrays;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class SolrCaller implements SearchCaller {
    private final SolrConfiguration solrConfiguration;

    @Override
    public SearchCallerResponse findTasks(SearchCallerRequest request) {
        log.debug("Search for tasks: {}", request);
        if (request.allowedProjects().isEmpty()) {
            log.debug("No allowed projects, return empty list");
            return new SearchCallerResponse(List.of(), 0);
        }
        final var solrQuery = createQuery(request);
        try (var client = createClient()) {
            final var response = client.query(solrQuery);
            return toResponse(response);
        } catch (Exception e) {
            log.warn("Could not execute search {}", request, e);
            return new SearchCallerResponse(List.of(), 0);
        }
    }

    private SolrClient createClient() {
        return new HttpJdkSolrClient.Builder(solrConfiguration.getUrl()).build();
    }

    private SolrQuery createQuery(SearchCallerRequest request) {
        final var fq = request.allowedProjects().stream()
                .map(project -> "project:" + project.getKey())
                .reduce((a, b) -> a + " OR " + b)
                .orElse("*:*");
        final var searchString = ClientUtils.escapeQueryChars(request.query());
        final var query = new SolrQuery(searchString);
        query.set(CommonParams.FL, "*");
        query.set(DisMaxParams.QF, "key^20 title^5 _text_");
        query.set("defType", "edismax");
        query.addFilterQuery(fq);
        query.setRows(request.pageSize());
        query.setStart((request.page() - 1) * request.pageSize());
        return query;
    }

    private SearchCallerResponse toResponse(QueryResponse response) {
        final var result = response.getBeans(TaskSolrDocument.class).stream()
                .map(TaskSolrDocument::toTaskView)
                .toList();
        final var total = (int) response.getResults().getNumFound();
        log.debug("Get {} of {} results", result.size(), total);
        return new SearchCallerResponse(result, total);
    }

    @Override
    public void informUpdatedTasks(Task... tasks) {
        log.debug("Update {} tasks", tasks.length);
        if (tasks.length == 0) return;
        final var documents = Arrays.stream(tasks)
                .map(TaskSolrDocument::fromTask)
                .toList();
        try (var client = createClient()) {
            client.addBeans(documents);
            client.commit();
            log.info("Updated {} tasks", tasks.length);
        } catch (Exception e) {
            log.warn("Could not update tasks. Index needs to be recreated", e);
        }
    }

    @Override
    public void informDeletedTasks(Task... tasks) {
        log.debug("Delete {} tasks", tasks.length);
        if (tasks.length == 0) return;
        try (var client = createClient()) {
            for (var task : tasks) {
                client.deleteByQuery("key:" + task.getKey());
            }
            client.commit();
            log.info("Deleted {} tasks", tasks.length);
        } catch (Exception e) {
            log.warn("Could not update tasks. Index needs to be recreated", e);
        }
    }

    @Override
    public void clear() {
        log.debug("Clear index");
        try (var client = createClient()) {
            final var response = client.deleteByQuery("*:*");
            client.commit();
            log.info("Deleted {} documents", response.getStatus());
        } catch (Exception e) {
            log.warn("Could not delete index. Index needs to be recreated", e);
        }
    }
}
