package site.gutschi.humble.spring.integration.keycloak;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.ResponseBodyData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import site.gutschi.humble.spring.common.test.KeycloakContainer;
import site.gutschi.humble.spring.users.api.UpdateUserUseCase;
import site.gutschi.humble.spring.users.model.User;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.regex.Pattern;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.startsWith;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
class SecurityConfigurationTest {
    @Container
    static final KeycloakContainer container = new KeycloakContainer().withConfigFile("keycloak-config.json");
    @LocalServerPort
    int port;
    @MockitoBean
    private UpdateUserUseCase updateUserUseCase;

    @DynamicPropertySource
    static void keycloakProperties(DynamicPropertyRegistry registry) {
        container.registerProperties(registry);
    }

    @BeforeEach
    void setup() {
        Mockito.when(updateUserUseCase.updateUserAfterLogin(Mockito.any())).thenAnswer(i -> {
            final var request = (UpdateUserUseCase.UpdateUserRequest) i.getArgument(0);
            return User.builder().email(request.email()).name(request.name()).build();
        });
    }

    @Test
    void normalUser() {
        final var userData = loginAndGetUserData("dev@example.com", "dev");
        assertThat(userData).contains("User: Dev Develop <dev@example.com>");
        assertThat(userData).contains("SystemAdmin: false");
        Mockito.verify(updateUserUseCase, Mockito.times(1)).updateUserAfterLogin(new UpdateUserUseCase.UpdateUserRequest("dev@example.com", "Dev Develop"));
    }

    @Test
    void systemAdmin() {
        final var userData = loginAndGetUserData("admin@example.com", "admin");
        assertThat(userData).contains("User: Admin Administrator <admin@example.com>");
        assertThat(userData).contains("SystemAdmin: true");
        Mockito.verify(updateUserUseCase, Mockito.times(1)).updateUserAfterLogin(new UpdateUserUseCase.UpdateUserRequest("admin@example.com", "Admin Administrator"));
    }

    private String loginAndGetUserData(String username, String password) {
        final var applicationHostPort = "http://localhost:" + port;
        final var keycloakHostPort = container.getKeycloakBaseUrl();

        //@formatter:off
        //First page is just a redirect to the idp page on the application itself.
        final var initialRedirect = RestAssured
        .given()
            .redirects().follow(false)
        .when().get("http://localhost:" + port + "/test")
        .then()
            .statusCode(302)
            .header("Location", startsWith(applicationHostPort))
        .extract();

        //A 2nd page is a redirect to keycloak
        final var redirectToKeycloak =  RestAssured
        .given()
            .redirects().follow(false)
            .cookies(initialRedirect.cookies())
        .when().get(getRedirectUrl(initialRedirect))
        .then()
            .statusCode(302)
            .header("Location", startsWith(keycloakHostPort))
        .extract();

        //The login page itself
        final var loginPage =  RestAssured
        .when().get(getRedirectUrl(redirectToKeycloak))
        .then()
            .statusCode(200)
            .body(containsString("Sign In"))
        .extract();

        // Fill out the login form
        final var redirectToApplication =  RestAssured
        .given()
            .cookies(loginPage.cookies())
            .formParam("username", username)
            .formParam("password", password)
            .formParam("credentialId")
        .when().post(getActionUrl(loginPage))
        .then()
            .statusCode(302)
            .header("Location", startsWith(applicationHostPort))
        .extract();

        // Redirect to the initial page
        final var redirectToInitialPage =  RestAssured
        .given()
            .redirects().follow(false)
            .cookies(initialRedirect.cookies())
        .when().get(getRedirectUrl(redirectToApplication))
        .then()
            .statusCode(302)
            .header("Location", startsWith(applicationHostPort))
        .extract();

        // The initial page
        return RestAssured
        .given()
            .redirects().follow(false)
            .cookies(redirectToInitialPage.cookies())
        .when().get(getRedirectUrl(redirectToInitialPage))
        .then()
            .statusCode(200)
        .extract().asString();
        //@formatter:on
    }

    private String getRedirectUrl(ExtractableResponse<?> htmlResponse) {
        return URLDecoder.decode(htmlResponse.header("Location"), StandardCharsets.UTF_8);
    }

    private String getActionUrl(ResponseBodyData htmlResponse) {
        final var m = Pattern.compile("action=\"([^\"]*)\"").matcher(htmlResponse.asString());
        assertThat(m.find(0)).isTrue();
        final var htmlLink = m.group(1);
        return htmlLink.replaceAll("&amp;", "&");
    }
}