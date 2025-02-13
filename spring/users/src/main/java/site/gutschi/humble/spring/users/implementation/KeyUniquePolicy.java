package site.gutschi.humble.spring.users.implementation;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import site.gutschi.humble.spring.users.api.KeyNotUniqueException;
import site.gutschi.humble.spring.users.ports.ProjectRepository;
import site.gutschi.humble.spring.users.ports.UserRepository;

@Service
@RequiredArgsConstructor
public class KeyUniquePolicy {
    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;

    public void ensureProjectKeyUnique(String projectKey) {
        final var existing = projectRepository.findByKey(projectKey);
        if (existing.isPresent())
            throw KeyNotUniqueException.projectKeyAlreadyExists(projectKey);
    }

    public void ensureUserMailUnique(String mail) {
        final var existing = userRepository.findByMail(mail);
        if (existing.isPresent())
            throw KeyNotUniqueException.userMailAlreadyExists(mail);
    }
}
