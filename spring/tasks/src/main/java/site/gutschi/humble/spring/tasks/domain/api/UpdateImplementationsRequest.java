package site.gutschi.humble.spring.tasks.domain.api;

import jakarta.validation.constraints.NotNull;

import java.net.URL;

public record UpdateImplementationsRequest(@NotNull URL url) {
}
