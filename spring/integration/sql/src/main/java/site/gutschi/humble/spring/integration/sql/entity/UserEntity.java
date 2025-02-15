package site.gutschi.humble.spring.integration.sql.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import site.gutschi.humble.spring.users.model.User;

@Data
@Entity(name = "userinfo")
public class UserEntity {
    @Id
    @NotBlank
    private String email;
    private String name;
    private String password;
    private boolean systemAdmin;

    public static UserEntity fromModel(User user) {
        final var entity = new UserEntity();
        entity.setEmail(user.getEmail());
        entity.setName(user.getName());
        entity.setPassword(user.getPassword());
        entity.setSystemAdmin(user.isSystemAdmin());
        return entity;
    }

    public User toModel() {
        return User.builder()
                .email(this.email)
                .name(this.name)
                .password(this.password)
                .systemAdmin(this.systemAdmin)
                .build();
    }
}
