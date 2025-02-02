package site.gutschi.humble.spring.tasks.domain.ports;

import java.net.URL;
import java.util.Collection;

public record CheckImplementationsResponse(
        Collection<CheckImplementationsResponseEntry> entries) {

    public record CheckImplementationsResponseEntry(String taskKey, URL url, String description) {
    }
}
