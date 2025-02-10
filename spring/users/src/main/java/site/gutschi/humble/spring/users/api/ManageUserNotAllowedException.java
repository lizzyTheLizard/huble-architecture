package site.gutschi.humble.spring.users.api;

import lombok.Getter;

@Getter
public class ManageUserNotAllowedException extends RuntimeException {
    private final String email;

    public ManageUserNotAllowedException(String email) {
        super("You are not allowed to manage user '" + email + "'");
        this.email = email;
    }
}
