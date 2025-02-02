package site.gutschi.humble.spring.tasks.model;

import lombok.Builder;

import java.time.Instant;

@Builder
public record TaskHistoryEntry(String user, Instant timestamp,
                               TaskHistoryType type, String field,
                               String oldValue, String newValue) {
    @SuppressWarnings("unused") // Used in Thymeleaf templates
    public String description() {
        return switch (type) {
            case CREATED -> "Task created";
            case COMMENTED -> "Comment added";
            case IMPLEMENTED -> "Implementation added";
            case STATUS_CHANGED -> String.format("Status changed from %s to %s", oldValue, newValue);
            case FIELD_CHANGED -> String.format("Field %s changed from %s to %s", field, oldValue, newValue);
            case TITLE_CHANGED -> String.format("Title changed %s to %s", oldValue, newValue);
            case DESCRIPTION_CHANGED -> String.format("Description changed %s to %s", oldValue, newValue);
            case DELETED -> "Task deleted";
            case ASSIGNED -> String.format("Task assigned to %s", newValue);
            case ESTIMATED -> String.format("Estimated time changed from %s to %s", oldValue, newValue);
        };
    }
}
