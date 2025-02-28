package site.gutschi.humble.spring.tasks.model;

import lombok.Builder;

import java.time.Instant;

@Builder
//TODO Modelling: Use User instead of String
public record TaskHistoryEntry(String user, Instant timestamp,
                               TaskHistoryType type, String field,
                               String oldValue, String newValue) {
    @SuppressWarnings("unused") //Used implicitly through UI
    //TODO UI: Internationalization
    public String description() {
        return switch (type) {
            case CREATED -> "Task created";
            case COMMENTED -> "Comment added";
            case EDITED -> String.format("%s changed from %s to %s", field, oldValue, newValue);
            case DELETED -> "Task deleted";
        };
    }
}
