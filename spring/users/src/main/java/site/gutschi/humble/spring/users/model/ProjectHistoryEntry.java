package site.gutschi.humble.spring.users.model;

import lombok.Builder;

import java.time.Instant;

@Builder
public record ProjectHistoryEntry(User user, Instant timestamp, ProjectHistoryType type,
                                  User affectedUser, String oldValue, String newValue) {
    @SuppressWarnings("unused") //Used implicitly through UI
    public String description() {
        return switch (type) {
            case CREATED -> "Project created";
            case USER_ADDED -> String.format("User %s added as %s", affectedUser.getEmail(), newValue);
            case USER_REMOVED -> String.format("User %s removed", affectedUser.getEmail());
            case USER_ROLE_CHANGED ->
                    String.format("User %s changed from %s to %s", affectedUser.getEmail(), oldValue, newValue);
            case NAME_CHANGED -> String.format("Name changed to %s", newValue);
            case ACTIVATE_CHANGED -> String.format("Activation changed to %s", newValue);
        };
    }
}
