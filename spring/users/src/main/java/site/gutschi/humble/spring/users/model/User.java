package site.gutschi.humble.spring.users.model;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Builder
@AllArgsConstructor
public class User {
    private final String email;
    private final String password;
    private final boolean systemAdmin;
    @Setter
    private String name;
}
