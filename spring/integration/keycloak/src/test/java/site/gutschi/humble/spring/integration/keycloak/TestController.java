package site.gutschi.humble.spring.integration.keycloak;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import site.gutschi.humble.spring.common.api.CurrentUserApi;

@RestController
@SuppressWarnings("unused") // Used implicitly by the test
public class TestController {
    private final CurrentUserApi currentUserApi;

    public TestController(CurrentUserApi currentUserApi) {
        this.currentUserApi = currentUserApi;
    }

    @GetMapping("/test")
    public String test() {
        return "User: " + currentUserApi.currentEmail() + ", SystemAdmin: " + currentUserApi.isSystemAdmin();
    }
}
