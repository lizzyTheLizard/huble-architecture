package site.gutschi.humble.spring.integration.sql;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import site.gutschi.humble.spring.integration.sql.entity.CommentEntity;
import site.gutschi.humble.spring.integration.sql.entity.NextIdEntity;
import site.gutschi.humble.spring.integration.sql.entity.TaskEntity;
import site.gutschi.humble.spring.integration.sql.entity.TaskHistoryEntryEntity;
import site.gutschi.humble.spring.integration.sql.repo.NextIdEntityRepository;
import site.gutschi.humble.spring.integration.sql.repo.ProjectEntityRepository;
import site.gutschi.humble.spring.integration.sql.repo.TaskEntityRepository;
import site.gutschi.humble.spring.tasks.model.Comment;
import site.gutschi.humble.spring.tasks.model.Task;
import site.gutschi.humble.spring.tasks.model.TaskHistoryEntry;
import site.gutschi.humble.spring.tasks.model.TaskKey;
import site.gutschi.humble.spring.tasks.ports.TaskRepository;
import site.gutschi.humble.spring.users.model.Project;

import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class JpaTaskRepository implements TaskRepository {
    private final TaskEntityRepository taskEntityRepository;
    private final ProjectEntityRepository projectEntityRepository;
    private final NextIdEntityRepository nextIdEntityRepository;
    private final JpaUserRepository jpaUserRepository;
    private final JpaProjectRepository jpaProjectRepository;

    @Override
    public Optional<Task> findByKey(String taskKey) {
        return taskEntityRepository
                .findById(taskKey)
                .map(this::toModel);
    }

    @Override
    public void save(Task task) {
        final var entity = fromModel(task);
        taskEntityRepository.save(entity);

    }

    @Override
    public int nextId(Project project) {
        final var name = "PROJECT-" + project.getKey();
        final int nextId = nextIdEntityRepository.findById(name)
                .map(NextIdEntity::getNextId)
                .orElse(1);
        final var newEntity = new NextIdEntity();
        newEntity.setName(name);
        newEntity.setNextId(nextId + 1);
        nextIdEntityRepository.save(newEntity);
        return nextId;
    }

    @Override
    public Set<Task> findByProject(Project project) {
        final var projectEntity = projectEntityRepository.getReferenceById(project.getKey());
        return taskEntityRepository.findByProject(projectEntity).stream()
                .map(this::toModel)
                .collect(Collectors.toSet());
    }

    public TaskEntity fromModel(Task task) {
        final var entity = new TaskEntity();
        entity.setProject(projectEntityRepository.getReferenceById(task.getProject().getKey()));
        entity.setKey(task.getKey().toString());
        entity.setCreator(jpaUserRepository.fromModel(task.getCreator()));
        entity.setComments(task.getComments().stream()
                .map(c -> fromModel(c, entity))
                .collect(Collectors.toSet()));
        entity.setHistoryEntries(task.getHistoryEntries().stream()
                .map(h -> fromModel(h, entity))
                .collect(Collectors.toSet()));
        entity.setEstimation(task.getEstimation().orElse(null));
        entity.setStatus(task.getStatus());
        entity.setTitle(task.getTitle());
        entity.setDescription(task.getDescription());
        entity.setAssignee(task.getAssignee().map(jpaUserRepository::fromModel).orElse(null));
        entity.setDeleted(task.isDeleted());
        return entity;
    }

    public TaskHistoryEntryEntity fromModel(TaskHistoryEntry taskHistoryEntry, TaskEntity task) {
        final var entity = new TaskHistoryEntryEntity();
        entity.setTask(task);
        entity.setUser(jpaUserRepository.fromModel(taskHistoryEntry.user()));
        entity.setTimestamp(taskHistoryEntry.timestamp());
        entity.setType(taskHistoryEntry.type());
        entity.setField(taskHistoryEntry.field());
        entity.setOldValue(taskHistoryEntry.oldValue());
        entity.setNewValue(taskHistoryEntry.newValue());
        return entity;
    }

    public Task toModel(TaskEntity entity) {
        final var key = TaskKey.fromString(entity.getKey());
        return Task.builder()
                .id(key.taskId())
                .project(jpaProjectRepository.toModel(entity.getProject()))
                .creator(jpaUserRepository.toModel(entity.getCreator()))
                .comments(entity.getComments().stream()
                        .map(this::toModel)
                        .collect(Collectors.toList()))
                .historyEntries(entity.getHistoryEntries().stream()
                        .map(this::toModel)
                        .collect(Collectors.toList()))
                .estimation(entity.getEstimation())
                .status(entity.getStatus())
                .title(entity.getTitle())
                .description(entity.getDescription())
                .assignee(entity.getAssignee() == null ? null : jpaUserRepository.toModel(entity.getAssignee()))
                .deleted(entity.isDeleted())
                .build();
    }

    public TaskHistoryEntry toModel(TaskHistoryEntryEntity entity) {
        return new TaskHistoryEntry(jpaUserRepository.toModel(entity.getUser()), entity.getTimestamp(), entity.getType(), entity.getField(), entity.getOldValue(), entity.getNewValue());
    }


    public CommentEntity fromModel(Comment comment, TaskEntity task) {
        final var entity = new CommentEntity();
        entity.setTask(task);
        entity.setUser(jpaUserRepository.fromModel(comment.user()));
        entity.setText(comment.text());
        entity.setTimestamp(comment.timestamp());
        return entity;
    }

    public Comment toModel(CommentEntity entity) {
        return new Comment(jpaUserRepository.toModel(entity.getUser()), entity.getTimestamp(), entity.getText());
    }
}
