package site.gutschi.humble.spring.users.model;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Builder
@AllArgsConstructor
public class User {
    @Setter
    private String name;
    private String email;
    private String password;
    private boolean systemAdmin;
}
