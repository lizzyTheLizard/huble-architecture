package site.gutschi.humble.spring.tasks.domain.ports;

import site.gutschi.humble.spring.users.model.Project;

import java.util.Collection;

public record SearchCallerRequest(String query, int page, int pageSize, Collection<Project> allowedProjects) {
}
