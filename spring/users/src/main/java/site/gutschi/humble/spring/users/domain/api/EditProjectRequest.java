package site.gutschi.humble.spring.users.domain.api;

import java.util.UUID;

public record EditProjectRequest(String projectKey,
                                 String name,
                                 boolean active) {
}
