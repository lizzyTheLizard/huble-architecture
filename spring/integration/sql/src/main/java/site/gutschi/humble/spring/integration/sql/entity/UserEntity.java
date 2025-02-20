package site.gutschi.humble.spring.integration.sql.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import site.gutschi.humble.spring.users.model.User;

@Getter
@Setter
@Entity(name = "userinfo")
public class UserEntity {
    @Id
    @NotBlank
    private String email;
    private String name;

    public static UserEntity fromModel(User user) {
        final var entity = new UserEntity();
        entity.setEmail(user.getEmail());
        entity.setName(user.getName());
        return entity;
    }

    public User toModel() {
        return User.builder()
                .email(this.email)
                .name(this.name)
                .build();
    }
}
