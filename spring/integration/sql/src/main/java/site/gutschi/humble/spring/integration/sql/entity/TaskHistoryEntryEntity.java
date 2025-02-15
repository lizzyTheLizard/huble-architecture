package site.gutschi.humble.spring.integration.sql.entity;

import jakarta.persistence.*;
import lombok.Data;
import site.gutschi.humble.spring.integration.sql.repo.UserEntityRepository;
import site.gutschi.humble.spring.tasks.model.TaskHistoryEntry;
import site.gutschi.humble.spring.tasks.model.TaskHistoryType;

import java.time.Instant;

@Data
@Entity(name = "taskHistoryEntry")
public class TaskHistoryEntryEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    private UserEntity user;
    @ManyToOne
    private TaskEntity task;
    private Instant timestamp;
    @Enumerated(EnumType.STRING)
    private TaskHistoryType type;
    private String field;
    private String oldValue;
    private String newValue;

    public static TaskHistoryEntryEntity fromModel(TaskHistoryEntry taskHistoryEntry, TaskEntity task, UserEntityRepository repository) {
        final var entity = new TaskHistoryEntryEntity();
        entity.setTask(task);
        entity.setUser(repository.getReferenceById(taskHistoryEntry.user()));
        entity.setTimestamp(taskHistoryEntry.timestamp());
        entity.setType(taskHistoryEntry.type());
        entity.setField(taskHistoryEntry.field());
        entity.setOldValue(taskHistoryEntry.oldValue());
        entity.setNewValue(taskHistoryEntry.newValue());
        return entity;
    }

    public TaskHistoryEntry toModel() {
        return new TaskHistoryEntry(user.getEmail(), timestamp, type, field, oldValue, newValue);
    }
}
