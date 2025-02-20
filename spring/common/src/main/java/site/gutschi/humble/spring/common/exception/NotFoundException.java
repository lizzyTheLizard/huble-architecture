package site.gutschi.humble.spring.common.exception;

import lombok.Getter;

/**
 * Exception thrown when a resource is not found or not visible for the current user.
 */
@Getter
public class NotFoundException extends RuntimeException {
    private final String publicMessage;

    private NotFoundException(String publicMessage, String internalMessage) {
        super(internalMessage);
        this.publicMessage = publicMessage;
    }

    public static RuntimeException notFound(String type, String id, String currentUser) {
        final var publicMessage = String.format("%s '%s' does not exist or is not visible for user %s", type, id, currentUser);
        final var internalMessage = String.format("%s '%s' does not exist", type, id);
        return new NotFoundException(publicMessage, internalMessage);
    }

    public static RuntimeException notVisible(String type, String id, String currentUser) {
        final var publicMessage = String.format("%s '%s' does not exist or is not visible for user %s", type, id, currentUser);
        final var internalMessage = String.format("%s '%s' is not visible for user %s", type, id, currentUser);
        return new NotFoundException(publicMessage, internalMessage);
    }

}
