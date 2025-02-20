package site.gutschi.humble.spring.common.exception;

import lombok.Getter;

/**
 * Exception thrown when the input is invalid.
 */
@Getter
public class InvalidInputException extends RuntimeException {
    private final String publicMessage;

    public InvalidInputException(String message) {
        super(message);
        this.publicMessage = message;
    }
}
