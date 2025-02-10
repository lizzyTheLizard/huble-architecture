package site.gutschi.humble.spring.users.api;

import lombok.Getter;

@Getter
public class CreateProjectNotAllowedException extends RuntimeException {
    public CreateProjectNotAllowedException() {
        super("You cannot create new projects");
    }
}
