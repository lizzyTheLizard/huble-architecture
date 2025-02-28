package site.gutschi.humble.spring.tasks.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Singular;
import site.gutschi.humble.spring.common.api.CurrentUserApi;
import site.gutschi.humble.spring.common.helper.TimeHelper;

import java.util.*;

/**
 * A single task. It can be created, edited and deleted. Comments can be added to the task.
 * A task will keep a journal of each change.
 */
//TODO Modelling: Use User instead of String
public class Task {
    private final CurrentUserApi currentUserApi;
    private final int id;
    @Getter
    private final String projectKey;
    @Getter
    private final String creatorEmail;
    private final Set<Comment> comments;
    private final Set<TaskHistoryEntry> historyEntries;
    public Integer estimationOrNull;
    @Getter
    private TaskStatus status;
    @Getter
    private String title;
    @Getter
    private String description;
    private String assigneeEmailOrNull;
    @Getter
    private boolean deleted;

    @Builder
    public Task(CurrentUserApi currentUserApi, int id, String projectKey, String creatorEmail, TaskStatus status,
                String title, String description, String assigneeEmail, Integer estimation, boolean deleted,
                @Singular Collection<Comment> comments, @Singular Collection<TaskHistoryEntry> historyEntries) {
        this.currentUserApi = currentUserApi;
        this.id = id;
        this.projectKey = projectKey;
        this.creatorEmail = creatorEmail;
        this.status = status;
        this.title = title;
        this.description = description;
        this.assigneeEmailOrNull = assigneeEmail;
        this.estimationOrNull = estimation;
        this.deleted = deleted;
        this.comments = new HashSet<>(comments);
        this.historyEntries = new HashSet<>(historyEntries);
    }

    public static Task createNew(CurrentUserApi currentUserApi, String projectKey, int nextId, String title, String description) {
        final var historyEntry = TaskHistoryEntry.builder()
                .user(currentUserApi.currentEmail())
                .timestamp(TimeHelper.now())
                .type(TaskHistoryType.CREATED)
                .build();
        return Task.builder()
                .id(nextId)
                .currentUserApi(currentUserApi)
                .projectKey(projectKey)
                .creatorEmail(currentUserApi.currentEmail())
                .status(TaskStatus.FUNNEL)
                .title(title)
                .description(description)
                .deleted(false)
                .historyEntry(historyEntry)
                .build();
    }

    public TaskKey getKey() {
        return new TaskKey(projectKey, id);
    }

    public void setStatus(TaskStatus status) {
        if (status == this.status) return;
        final var historyEntry = historyBuilder()
                .type(TaskHistoryType.EDITED)
                .field("Status")
                .oldValue(this.status.name())
                .newValue(status.name())
                .build();
        this.status = status;
        this.historyEntries.add(historyEntry);
    }

    public void setTitle(String title) {
        if (Objects.equals(title, this.title)) return;
        final var historyEntry = historyBuilder()
                .type(TaskHistoryType.EDITED)
                .field("Title")
                .oldValue(this.title)
                .newValue(title)
                .build();
        this.historyEntries.add(historyEntry);
        this.title = title;
    }

    public void setDescription(String description) {
        if (Objects.equals(description, this.description)) return;
        final var historyEntry = historyBuilder()
                .type(TaskHistoryType.EDITED)
                .field("Description")
                .oldValue(this.description)
                .newValue(description)
                .build();
        this.historyEntries.add(historyEntry);
        this.description = description;
    }

    public Optional<String> getAssigneeEmail() {
        return Optional.ofNullable(this.assigneeEmailOrNull);
    }

    public void setAssigneeEmail(String assigneeEmailOrNull) {
        if (Objects.equals(assigneeEmailOrNull, this.assigneeEmailOrNull)) return;
        final var historyEntry = historyBuilder()
                .type(TaskHistoryType.EDITED)
                .field("Assignee")
                .oldValue(this.assigneeEmailOrNull)
                .newValue(assigneeEmailOrNull)
                .build();
        this.assigneeEmailOrNull = assigneeEmailOrNull;
        this.historyEntries.add(historyEntry);
    }

    public Optional<Integer> getEstimation() {
        return Optional.ofNullable(this.estimationOrNull);
    }

    public void setEstimation(Integer estimationOrNull) {
        if (Objects.equals(estimationOrNull, this.estimationOrNull)) return;
        final var historyEntry = historyBuilder()
                .type(TaskHistoryType.EDITED)
                .field("Estimation")
                .oldValue(this.estimationOrNull == null ? null : this.estimationOrNull.toString())
                .newValue(estimationOrNull == null ? null : estimationOrNull.toString())
                .build();
        this.estimationOrNull = estimationOrNull;
        this.historyEntries.add(historyEntry);
    }

    public void setDeleted() {
        final var historyEntry = historyBuilder()
                .type(TaskHistoryType.DELETED)
                .build();
        this.deleted = true;
        this.historyEntries.add(historyEntry);
    }

    public void addComment(String text) {
        final var user = currentUserApi.currentEmail();
        final var time = TimeHelper.now();
        final var historyEntry = historyBuilder()
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

    private TaskHistoryEntry.TaskHistoryEntryBuilder historyBuilder() {
        return TaskHistoryEntry.builder().user(currentUserApi.currentEmail()).timestamp(TimeHelper.now());
    }
}
