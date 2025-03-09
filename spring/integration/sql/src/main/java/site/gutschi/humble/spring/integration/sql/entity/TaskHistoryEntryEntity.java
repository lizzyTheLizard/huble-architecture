package site.gutschi.humble.spring.integration.sql.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import site.gutschi.humble.spring.tasks.model.TaskHistoryType;

import java.time.Instant;

@Getter
@Setter
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
}
