package site.gutschi.humble.spring.tasks.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Singular;
import lombok.extern.slf4j.Slf4j;
import site.gutschi.humble.spring.common.api.CurrentUserApi;
import site.gutschi.humble.spring.common.helper.TimeHelper;

import java.util.*;

@Slf4j
public class Task {
    private final CurrentUserApi currentUserApi;
    private final int id;
    @Getter
    private final String projectKey;
    @Getter
    private final String creatorEmail;
    private final Set<Comment> comments = new HashSet<>();
    private final Set<TaskHistoryEntry> historyEntries = new HashSet<>();
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
    @SuppressWarnings("unused") // Used via builder
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
        if (comments != null) this.comments.addAll(comments);
        if (historyEntries != null) this.historyEntries.addAll(historyEntries);
    }

    public String getKey() {
        return projectKey + "-" + id;
    }

    public void setStatus(TaskStatus status) {
        if (status == this.status) return;
        final var historyEntry = historyBuilder()
                .type(TaskHistoryType.STATUS_CHANGED)
                .oldValue(this.status.name())
                .newValue(status.name())
                .build();
        this.status = status;
        this.historyEntries.add(historyEntry);
        log.debug("Task {} edited: {}", getKey(), historyEntry);
    }

    public void setTitle(String title) {
        if (Objects.equals(title, this.title)) return;
        final var historyEntry = historyBuilder()
                .type(TaskHistoryType.TITLE_CHANGED)
                .oldValue(this.title)
                .newValue(title)
                .build();
        this.historyEntries.add(historyEntry);
        this.title = title;
        log.debug("Task {} edited: {}", getKey(), historyEntry);
    }

    public void setDescription(String description) {
        if (Objects.equals(description, this.description)) return;
        final var historyEntry = historyBuilder()
                .type(TaskHistoryType.DESCRIPTION_CHANGED)
                .oldValue(this.description)
                .newValue(description)
                .build();
        this.historyEntries.add(historyEntry);
        this.description = description;
        log.debug("Task {} edited: {}", getKey(), historyEntry);
    }

    public Optional<String> getAssigneeEmail() {
        return Optional.ofNullable(this.assigneeEmailOrNull);
    }

    public void setAssigneeEmail(String assigneeEmailOrNull) {
        if (Objects.equals(assigneeEmailOrNull, this.assigneeEmailOrNull)) return;
        final var historyEntry = historyBuilder()
                .type(TaskHistoryType.ASSIGNED)
                .oldValue(this.assigneeEmailOrNull)
                .newValue(assigneeEmailOrNull)
                .build();
        this.assigneeEmailOrNull = assigneeEmailOrNull;
        this.historyEntries.add(historyEntry);
        log.debug("Task {} edited: {}", getKey(), historyEntry);
    }

    public Optional<Integer> getEstimation() {
        return Optional.ofNullable(this.estimationOrNull);
    }

    public void setEstimation(Integer estimationOrNull) {
        if (Objects.equals(estimationOrNull, this.estimationOrNull)) return;
        final var historyEntry = historyBuilder()
                .type(TaskHistoryType.ESTIMATED)
                .oldValue(this.estimationOrNull == null ? null : this.estimationOrNull.toString())
                .newValue(estimationOrNull == null ? null : estimationOrNull.toString())
                .build();
        this.estimationOrNull = estimationOrNull;
        this.historyEntries.add(historyEntry);
        log.debug("Task {} edited: {}", getKey(), historyEntry);
    }

    public void setDeleted() {
        final var historyEntry = historyBuilder()
                .type(TaskHistoryType.DELETED)
                .build();
        this.deleted = true;
        this.historyEntries.add(historyEntry);
        log.debug("Task {} edited: {}", getKey(), historyEntry);
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
        log.debug("Task {} edited: {}", getKey(), historyEntry);
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
