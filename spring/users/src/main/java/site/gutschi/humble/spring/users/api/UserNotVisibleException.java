package site.gutschi.humble.spring.users.api;

import lombok.Getter;

@Getter
public class UserNotVisibleException extends RuntimeException {
    private final String userEmail;

    public UserNotVisibleException(String userEmail) {
        super("User '" + userEmail + "' is not visible");
        this.userEmail = userEmail;
    }
}
