package site.gutschi.humble.spring.tasks.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Singular;
import site.gutschi.humble.spring.common.api.TimeApi;
import site.gutschi.humble.spring.common.api.UserApi;

import java.net.URL;
import java.util.*;

public class Task {
    private final UserApi userApi;
    private final TimeApi timeApi;
    private final int id;
    @Getter
    private final String projectKey;
    @Getter
    private final String creatorEmail;
    private final List<Comment> comments = new LinkedList<>();
    private final List<Implementation> implementations = new LinkedList<>();
    private final List<TaskHistoryEntry> historyEntries = new LinkedList<>();
    private final Map<String, String> fields = new HashMap<>();
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
    public Task(UserApi userApi, TimeApi timeApi, int id, String projectKey, String creatorEmail, TaskStatus status, String title, String description, String assigneeEmail, Integer estimation, boolean deleted, @Singular Collection<Comment> comments, @Singular Collection<Implementation> implementations, @Singular Collection<TaskHistoryEntry> historyEntries, @Singular Map<String, String> fields) {
        this.userApi = userApi;
        this.timeApi = timeApi;
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
        if (implementations != null) this.implementations.addAll(implementations);
        if (historyEntries != null) this.historyEntries.addAll(historyEntries);
        if (fields != null) this.fields.putAll(fields);
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
        this.historyEntries.add(0, historyEntry);
    }

    public void setTitle(String title) {
        if (Objects.equals(title, this.title)) return;
        final var historyEntry = historyBuilder()
                .type(TaskHistoryType.TITLE_CHANGED)
                .oldValue(this.title)
                .newValue(title)
                .build();
        this.historyEntries.add(0, historyEntry);
        this.title = title;
    }

    public void setDescription(String description) {
        if (Objects.equals(description, this.description)) return;
        final var historyEntry = historyBuilder()
                .type(TaskHistoryType.DESCRIPTION_CHANGED)
                .oldValue(this.description)
                .newValue(description)
                .build();
        this.historyEntries.add(0, historyEntry);
        this.description = description;
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
        this.historyEntries.add(0, historyEntry);
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
        this.historyEntries.add(0, historyEntry);
    }

    public void setDeleted() {
        final var historyEntry = historyBuilder()
                .type(TaskHistoryType.DELETED)
                .build();
        this.deleted = true;
        this.historyEntries.add(0, historyEntry);
    }

    public void addComment(String text) {
        final var user = userApi.currentEmail();
        final var time = timeApi.now();
        final var historyEntry = historyBuilder()
                .timestamp(time)
                .type(TaskHistoryType.COMMENTED)
                .newValue(text)
                .build();
        final var comment = new Comment(user, time, text);
        this.comments.add(0, comment);
        this.historyEntries.add(0, historyEntry);
    }

    public Collection<Comment> getComments() {
        return Collections.unmodifiableCollection(this.comments);
    }

    public void addImplementation(URL url, String description) {
        final var time = timeApi.now();
        final var historyEntry = historyBuilder()
                .timestamp(time)
                .type(TaskHistoryType.IMPLEMENTED)
                .newValue(url.toString())
                .build();
        final var implementation = new Implementation(url, description);
        this.implementations.add(0, implementation);
        this.historyEntries.add(0, historyEntry);
    }

    public Collection<Implementation> getImplementations() {
        return Collections.unmodifiableCollection(this.implementations);
    }

    public Collection<TaskHistoryEntry> getHistoryEntries() {
        return Collections.unmodifiableCollection(this.historyEntries);
    }

    public void setAdditionalFields(Map<String, String> additionalField) {
        this.fields.keySet().stream().filter(key -> !additionalField.containsKey(key)).forEach(key -> setField(key, null));
        additionalField.forEach((key, newValue) -> {
            final var oldValue = this.fields.get(key);
            if (Objects.equals(oldValue, newValue)) return;
            setField(key, newValue);
        });
    }

    private void setField(String field, String valueOrNull) {
        final var oldValue = this.fields.get(field);
        if (Objects.equals(oldValue, valueOrNull)) return;
        if (valueOrNull == null) {
            this.fields.remove(field);
        } else {
            this.fields.put(field, valueOrNull);
        }
        final var historyEntry = historyBuilder()
                .type(TaskHistoryType.FIELD_CHANGED)
                .field(field)
                .oldValue(oldValue)
                .newValue(valueOrNull)
                .build();
        this.historyEntries.add(0, historyEntry);
    }

    public Map<String, String> getFields() {
        return Collections.unmodifiableMap(this.fields);
    }

    private TaskHistoryEntry.TaskHistoryEntryBuilder historyBuilder() {
        return TaskHistoryEntry.builder().user(userApi.currentEmail()).timestamp(timeApi.now());
    }
}
