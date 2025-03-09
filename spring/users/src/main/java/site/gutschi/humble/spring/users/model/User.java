package site.gutschi.humble.spring.users.model;


import lombok.*;

/**
 * A user in the systems. User are generated and updated after each login
 * and can then be assigned to projects.
 */
@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class User {
    private final String email;
    @Setter
    private String name;

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof User user) {
            return email.equals(user.email);
        }
        return false;
    }
}
