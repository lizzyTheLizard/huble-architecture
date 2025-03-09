package site.gutschi.humble.spring.integration.sql.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import site.gutschi.humble.spring.tasks.model.TaskStatus;

import java.util.Set;

@Getter
@Setter
@Entity(name = "task")
public class TaskEntity {
    @Id
    private String key;
    @ManyToOne
    private ProjectEntity project;
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
}
