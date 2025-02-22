package site.gutschi.humble.spring.integration.sql.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity(name = "nextId")
public class NextIdEntity {
    @Id
    @NotBlank
    private String name;
    private int nextId;
}
