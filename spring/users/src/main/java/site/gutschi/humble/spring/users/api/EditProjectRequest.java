package site.gutschi.humble.spring.users.api;

import java.util.Collection;

public record EditProjectRequest(String projectKey,
                                 String name,
                                 Collection<Integer> estimations,
                                 boolean active) {
}
