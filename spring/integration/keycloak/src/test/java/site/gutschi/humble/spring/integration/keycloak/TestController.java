package site.gutschi.humble.spring.integration.keycloak;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import site.gutschi.humble.spring.users.ports.CurrentUserInformation;

@RestController
@SuppressWarnings("unused") // Used implicitly by the test
public class TestController {
    private final CurrentUserInformation currentUserInformation;

    public TestController(CurrentUserInformation currentUserInformation) {
        this.currentUserInformation = currentUserInformation;
    }

    @GetMapping("/test")
    public String test() {
        return String.format("User: %s <%s>, SystemAdmin: %s",
                currentUserInformation.getCurrentUser().getName(),
                currentUserInformation.getCurrentUser().getEmail(),
                currentUserInformation.isSystemAdmin());
    }
}
