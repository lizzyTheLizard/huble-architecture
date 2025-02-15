package site.gutschi.humble.spring.integration.sql.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import site.gutschi.humble.spring.integration.sql.repo.UserEntityRepository;
import site.gutschi.humble.spring.tasks.model.Comment;

import java.time.Instant;

@Data
@Entity(name = "comment")
public class CommentEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    private TaskEntity task;
    @ManyToOne
    private UserEntity user;
    @NotBlank
    private String text;
    private Instant timestamp;

    public static CommentEntity fromModel(Comment comment, TaskEntity task, UserEntityRepository repository) {
        final var entity = new CommentEntity();
        entity.setTask(task);
        entity.setUser(repository.getReferenceById(comment.user()));
        entity.setText(comment.text());
        entity.setTimestamp(comment.timestamp());
        return entity;
    }

    public Comment toModel() {
        return new Comment(user.getEmail(), timestamp, text);
    }
}
