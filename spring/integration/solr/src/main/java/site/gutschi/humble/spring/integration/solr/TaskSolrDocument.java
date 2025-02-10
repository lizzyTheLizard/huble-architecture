package site.gutschi.humble.spring.integration.solr;

import lombok.Getter;
import lombok.Setter;
import org.apache.solr.client.solrj.beans.Field;
import site.gutschi.humble.spring.tasks.api.FindTasksResponse;
import site.gutschi.humble.spring.tasks.model.Comment;
import site.gutschi.humble.spring.tasks.model.Task;
import site.gutschi.humble.spring.tasks.model.TaskStatus;

import java.util.Collection;

@Getter
@Setter
public class TaskSolrDocument {
    @Field("key_s")
    public String key;
    @Field("project_s")
    public String project;
    @Field("title_t")
    public String title;
    @Field("assignee_s")
    public String assignee;
    @Field("creator_s")
    public String creator;
    @Field("description_t")
    public String description;
    @Field("status_s")
    public String status;
    @Field("estimation_i")
    public Integer estimation;
    @Field("comments_t")
    public Collection<String> comments;

    public TaskSolrDocument() {
    }

    public static TaskSolrDocument fromTask(Task task) {
        final var result = new TaskSolrDocument();
        result.key = task.getKey();
        result.project = task.getProjectKey();
        result.title = task.getTitle();
        result.description = task.getDescription();
        result.status = task.getStatus().name();
        result.estimation = task.getEstimation().orElse(null);
        result.assignee = task.getAssigneeEmail().orElse(null);
        result.creator = task.getCreatorEmail();
        result.comments = task.getComments().stream()
                .map(Comment::text)
                .toList();
        return result;
    }

    public FindTasksResponse.TaskFindView toTaskView() {
        return new FindTasksResponse.TaskFindView(key, title, assignee, TaskStatus.valueOf(status));
    }
}
