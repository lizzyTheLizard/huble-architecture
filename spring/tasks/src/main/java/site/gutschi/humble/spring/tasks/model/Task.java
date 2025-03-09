package site.gutschi.humble.spring.tasks.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Singular;
import site.gutschi.humble.spring.common.helper.TimeHelper;
import site.gutschi.humble.spring.users.model.Project;
import site.gutschi.humble.spring.users.model.User;

import java.util.*;

/**
 * A single task. It can be created, edited and deleted. Comments can be added to the task.
 * A task will keep a journal of each change.
 */
public class Task {
    private final int id;
    @Getter
    private final Project project;
    @Getter
    private final User creator;
    private final Set<Comment> comments;
    private final Set<TaskHistoryEntry> historyEntries;
    public Integer estimationOrNull;
    @Getter
    private TaskStatus status;
    @Getter
    private String title;
    @Getter
    private String description;
    private User assigneeOrNull;
    @Getter
    private boolean deleted;

    @Builder
    public Task(int id, Project project, User creator, TaskStatus status,
                String title, String description, User assignee, Integer estimation, boolean deleted,
                @Singular Collection<Comment> comments, @Singular Collection<TaskHistoryEntry> historyEntries) {
        this.id = id;
        this.project = project;
        this.creator = creator;
        this.status = status;
        this.title = title;
        this.description = description;
        this.assigneeOrNull = assignee;
        this.estimationOrNull = estimation;
        this.deleted = deleted;
        this.comments = new HashSet<>(comments);
        this.historyEntries = new HashSet<>(historyEntries);
    }

    public static Task createNew(Project project, int nextId, String title, String description, User creator) {
        final var historyEntry = TaskHistoryEntry.builder()
                .user(creator)
                .timestamp(TimeHelper.now())
                .type(TaskHistoryType.CREATED)
                .build();
        return Task.builder()
                .id(nextId)
                .project(project)
                .creator(creator)
                .status(TaskStatus.FUNNEL)
                .title(title)
                .description(description)
                .deleted(false)
                .historyEntry(historyEntry)
                .build();
    }

    public TaskKey getKey() {
        return new TaskKey(project.getKey(), id);
    }

    public void setStatus(TaskStatus status, User user) {
        if (status == this.status) return;
        final var historyEntry = TaskHistoryEntry.builder()
                .user(user)
                .timestamp(TimeHelper.now())
                .type(TaskHistoryType.EDITED)
                .field("Status")
                .oldValue(this.status.name())
                .newValue(status.name())
                .build();
        this.status = status;
        this.historyEntries.add(historyEntry);
    }

    public void setTitle(String title, User user) {
        if (Objects.equals(title, this.title)) return;
        final var historyEntry = TaskHistoryEntry.builder()
                .user(user)
                .timestamp(TimeHelper.now())
                .type(TaskHistoryType.EDITED)
                .field("Title")
                .oldValue(this.title)
                .newValue(title)
                .build();
        this.historyEntries.add(historyEntry);
        this.title = title;
    }

    public void setDescription(String description, User user) {
        if (Objects.equals(description, this.description)) return;
        final var historyEntry = TaskHistoryEntry.builder()
                .user(user)
                .timestamp(TimeHelper.now())
                .type(TaskHistoryType.EDITED)
                .field("Description")
                .oldValue(this.description)
                .newValue(description)
                .build();
        this.historyEntries.add(historyEntry);
        this.description = description;
    }

    public Optional<User> getAssignee() {
        return Optional.ofNullable(this.assigneeOrNull);
    }

    public void setAssignee(User assigneeOrNull, User user) {
        if (Objects.equals(assigneeOrNull, this.assigneeOrNull)) return;
        final var historyEntryBuilder = TaskHistoryEntry.builder()
                .user(user)
                .timestamp(TimeHelper.now())
                .type(TaskHistoryType.EDITED)
                .field("Assignee");
        if (this.assigneeOrNull != null) {
            historyEntryBuilder.oldValue(this.assigneeOrNull.getEmail());
        }
        if (assigneeOrNull != null) {
            historyEntryBuilder.newValue(assigneeOrNull.getEmail());
        }
        final var historyEntry = historyEntryBuilder.build();
        this.assigneeOrNull = assigneeOrNull;
        this.historyEntries.add(historyEntry);
    }

    public Optional<Integer> getEstimation() {
        return Optional.ofNullable(this.estimationOrNull);
    }

    public void setEstimation(Integer estimationOrNull, User user) {
        if (Objects.equals(estimationOrNull, this.estimationOrNull)) return;
        final var historyEntry = TaskHistoryEntry.builder()
                .user(user)
                .timestamp(TimeHelper.now())
                .type(TaskHistoryType.EDITED)
                .field("Estimation")
                .oldValue(this.estimationOrNull == null ? null : this.estimationOrNull.toString())
                .newValue(estimationOrNull == null ? null : estimationOrNull.toString())
                .build();
        this.estimationOrNull = estimationOrNull;
        this.historyEntries.add(historyEntry);
    }

    public void setDeleted(User user) {
        final var historyEntry = TaskHistoryEntry.builder()
                .user(user)
                .timestamp(TimeHelper.now())
                .type(TaskHistoryType.DELETED)
                .build();
        this.deleted = true;
        this.historyEntries.add(historyEntry);
    }

    public void addComment(String text, User user) {
        final var time = TimeHelper.now();
        final var historyEntry = TaskHistoryEntry.builder()
                .user(user)
                .timestamp(TimeHelper.now())
                .timestamp(time)
                .type(TaskHistoryType.COMMENTED)
                .newValue(text)
                .build();
        final var comment = new Comment(user, time, text);
        this.comments.add(comment);
        this.historyEntries.add(historyEntry);
    }

    public Set<Comment> getComments() {
        return Collections.unmodifiableSet(this.comments);
    }

    public Set<TaskHistoryEntry> getHistoryEntries() {
        return Collections.unmodifiableSet(this.historyEntries);
    }
}
