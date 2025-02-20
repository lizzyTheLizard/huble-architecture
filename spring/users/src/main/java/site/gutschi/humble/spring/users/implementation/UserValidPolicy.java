package site.gutschi.humble.spring.users.implementation;

import lombok.RequiredArgsConstructor;
import org.apache.commons.validator.routines.EmailValidator;
import org.springframework.stereotype.Service;
import site.gutschi.humble.spring.common.exception.InvalidInputException;
import site.gutschi.humble.spring.users.model.User;

@Service
@RequiredArgsConstructor
public class UserValidPolicy {
    public void ensureUserValid(User user) {
        if (user.getName() == null || user.getName().isBlank())
            throw new InvalidInputException("User name must not be empty");
        if (user.getEmail() == null || user.getEmail().isBlank())
            throw new InvalidInputException("User email must not be empty");
        if (!EmailValidator.getInstance().isValid(user.getEmail()))
            throw new InvalidInputException("User email must be valid");
    }
}
