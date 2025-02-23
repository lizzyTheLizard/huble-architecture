package site.gutschi.humble.spring.integration.solr;

import lombok.Getter;
import lombok.Setter;
import org.apache.solr.client.solrj.beans.Field;
import site.gutschi.humble.spring.tasks.model.Comment;
import site.gutschi.humble.spring.tasks.model.Task;
import site.gutschi.humble.spring.tasks.model.TaskKey;
import site.gutschi.humble.spring.tasks.model.TaskStatus;
import site.gutschi.humble.spring.tasks.usecases.ViewTasksUseCase;

import java.util.List;

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
    public List<String> comments;

    public TaskSolrDocument() {
    }

    public static TaskSolrDocument fromTask(Task task) {
        final var result = new TaskSolrDocument();
        result.key = task.getKey().toString();
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

    public ViewTasksUseCase.TaskFindView toTaskView() {
        final var key = TaskKey.fromString(this.key);
        return new ViewTasksUseCase.TaskFindView(key, title, assignee, TaskStatus.valueOf(status));
    }
}
