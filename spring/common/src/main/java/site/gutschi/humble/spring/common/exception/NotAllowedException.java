package site.gutschi.humble.spring.common.exception;

import lombok.Getter;

/**
 * Exception thrown when the operation is not allowed.
 */
@Getter
public class NotAllowedException extends RuntimeException {
    private final String publicMessage;

    private NotAllowedException(String publicMessage, String internalMessage) {
        super(internalMessage);
        this.publicMessage = publicMessage;
    }

    public static RuntimeException notAllowed(String type, String id, String operation, String currentUser) {
        final var message = String.format("User %s is not allowed to %s %s '%s'", currentUser, operation, type, id);
        return new NotAllowedException(message, message);
    }

    public static RuntimeException notAllowed(String type, String operation, String currentUser) {
        final var message = String.format("User %s is not allowed to %s %s", currentUser, operation, type);
        return new NotAllowedException(message, message);
    }

    public static RuntimeException projectNotActive(String id) {
        final var message = String.format("Project '%s' is no longer active", id);
        return new NotAllowedException(message, message);
    }
}
