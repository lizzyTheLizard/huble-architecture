package site.gutschi.humble.spring.users.model;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

/**
 * A user in the systems. User are generated and updated after each login
 * and can then be assigned to projects.
 */
@Getter
@Builder
@AllArgsConstructor
public class User {
    private final String email;
    @Setter
    private String name;
}
