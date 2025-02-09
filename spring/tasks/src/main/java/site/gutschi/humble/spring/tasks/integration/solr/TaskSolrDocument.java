package site.gutschi.humble.spring.tasks.integration.solr;

import lombok.Getter;
import lombok.Setter;
import org.apache.solr.client.solrj.beans.Field;
import site.gutschi.humble.spring.tasks.domain.api.FindTasksResponse;
import site.gutschi.humble.spring.tasks.model.Comment;
import site.gutschi.humble.spring.tasks.model.Task;
import site.gutschi.humble.spring.tasks.model.TaskStatus;

import java.util.Collection;

@Getter
@Setter
public class TaskSolrDocument {
    @Field
    public String key;
    @Field
    public String project;
    @Field
    public String title;
    @Field
    public String assignee;
    @Field
    public String creator;
    @Field
    public String description;
    @Field
    public String status;
    @Field
    public Integer estimation;
    @Field
    public Collection<String> comments;

    public TaskSolrDocument() {}

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

    public FindTasksResponse.TaskFindView toTaskView(){
        return new FindTasksResponse.TaskFindView(key, title, assignee, TaskStatus.valueOf(status));
    }
}
