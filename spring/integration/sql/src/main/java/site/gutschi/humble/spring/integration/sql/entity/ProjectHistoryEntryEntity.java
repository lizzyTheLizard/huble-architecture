package site.gutschi.humble.spring.integration.sql.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import site.gutschi.humble.spring.users.model.ProjectHistoryType;

import java.time.Instant;

@Getter
@Setter
@Entity(name = "projectHistoryEntry")
public class ProjectHistoryEntryEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    private UserEntity user;
    @ManyToOne
    private ProjectEntity project;
    private Instant timestamp;
    @Enumerated(EnumType.STRING)
    private ProjectHistoryType type;
    @ManyToOne
    private UserEntity affectedUser;
    private String oldValue;
    private String newValue;
}
