package site.gutschi.humble.spring.integration.ui.security;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import site.gutschi.humble.spring.users.ports.UserRepository;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {
    private final UserRepository userRepository;

    @Override
    public CustomUser loadUserByUsername(String username) throws UsernameNotFoundException {
        final var user = userRepository.findByMail(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        return new CustomUser(user);
    }
}
