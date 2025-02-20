package site.gutschi.humble.spring.integration.sql.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import site.gutschi.humble.spring.integration.sql.repo.UserEntityRepository;
import site.gutschi.humble.spring.tasks.model.Task;
import site.gutschi.humble.spring.tasks.model.TaskStatus;

import java.util.Set;
import java.util.stream.Collectors;

@Getter
@Setter
@Entity(name = "task")
public class TaskEntity {
    @Id
    private String key;
    @ManyToOne
    private UserEntity creator;
    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "task")
    private Set<CommentEntity> comments;
    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "task")
    private Set<TaskHistoryEntryEntity> historyEntries;
    private Integer estimation;
    @Enumerated(EnumType.STRING)
    private TaskStatus status;
    @NotBlank
    private String title;
    @NotBlank
    private String description;
    @ManyToOne
    private UserEntity assignee;
    private boolean deleted;

    public static TaskEntity fromModel(Task task, UserEntityRepository repository) {
        final var entity = new TaskEntity();
        entity.setKey(task.getKey());
        entity.setCreator(repository.getReferenceById(task.getCreatorEmail()));
        entity.setComments(task.getComments().stream()
                .map(c -> CommentEntity.fromModel(c, entity, repository))
                .collect(Collectors.toSet()));
        entity.setHistoryEntries(task.getHistoryEntries().stream()
                .map(h -> TaskHistoryEntryEntity.fromModel(h, entity, repository))
                .collect(Collectors.toSet()));
        entity.setEstimation(task.getEstimation().orElse(null));
        entity.setStatus(task.getStatus());
        entity.setTitle(task.getTitle());
        entity.setDescription(task.getDescription());
        entity.setAssignee(task.getAssigneeEmail().map(repository::getReferenceById).orElse(null));
        entity.setDeleted(task.isDeleted());
        return entity;
    }

    public Task toModel() {
        final var split = key.split("-");
        final var projectKey = split[0];
        final var id = Integer.parseInt(split[1]);
        return Task.builder()
                .id(id)
                .projectKey(projectKey)
                .creatorEmail(this.creator.getEmail())
                .comments(this.comments.stream()
                        .map(CommentEntity::toModel)
                        .collect(Collectors.toList()))
                .historyEntries(this.historyEntries.stream()
                        .map(TaskHistoryEntryEntity::toModel)
                        .collect(Collectors.toList()))
                .estimation(this.estimation)
                .status(this.status)
                .title(this.title)
                .description(this.description)
                .assigneeEmail(this.assignee == null ? null : this.assignee.getEmail())
                .deleted(this.deleted)
                .build();
    }
}
