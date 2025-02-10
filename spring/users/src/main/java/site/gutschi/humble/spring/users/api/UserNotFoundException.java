package site.gutschi.humble.spring.users.api;

import lombok.Getter;

@Getter
public class UserNotFoundException extends RuntimeException {
    private final String email;

    public UserNotFoundException(String email) {
        super("User '" + email + "' could not be found");
        this.email = email;
    }
}
