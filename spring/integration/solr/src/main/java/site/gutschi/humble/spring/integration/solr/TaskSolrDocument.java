package site.gutschi.humble.spring.integration.solr;

import lombok.Getter;
import lombok.Setter;
import org.apache.solr.client.solrj.beans.Field;
import site.gutschi.humble.spring.tasks.api.ViewTasksUseCase;
import site.gutschi.humble.spring.tasks.model.Comment;
import site.gutschi.humble.spring.tasks.model.Task;
import site.gutschi.humble.spring.tasks.model.TaskKey;
import site.gutschi.humble.spring.tasks.model.TaskStatus;
import site.gutschi.humble.spring.users.model.User;

import java.util.List;

@Getter
@Setter
public class TaskSolrDocument {
    @Field("key")
    public String key;
    @Field("project")
    public String project;
    @Field("title")
    public String title;
    @Field("assignee")
    public String assignee;
    @Field("creator")
    public String creator;
    @Field("description")
    public String description;
    @Field("status")
    public String status;
    @Field("estimation")
    public Integer estimation;
    @Field("comments")
    public List<String> comments;

    public TaskSolrDocument() {
    }

    public static TaskSolrDocument fromTask(Task task) {
        final var result = new TaskSolrDocument();
        result.key = task.getKey().toString();
        result.project = task.getProject().getKey();
        result.title = task.getTitle();
        result.description = task.getDescription();
        result.status = task.getStatus().name();
        result.estimation = task.getEstimation().orElse(null);
        result.assignee = task.getAssignee().map(User::getEmail).orElse(null);
        result.creator = task.getCreator().getEmail();
        result.comments = task.getComments().stream()
                .map(Comment::text)
                .toList();
        return result;
    }

    public ViewTasksUseCase.TaskFindView toTaskView() {
        final var key = TaskKey.fromString(this.key);
        return new ViewTasksUseCase.TaskFindView(key, title, assignee, TaskStatus.valueOf(status));
    }
}
