package site.gutschi.humble.spring.users.domain.api;

import java.util.UUID;

public record EditUserRequest(String name, String email) {
}
