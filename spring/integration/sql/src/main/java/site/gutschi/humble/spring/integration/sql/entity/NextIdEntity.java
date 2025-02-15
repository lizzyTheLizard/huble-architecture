package site.gutschi.humble.spring.integration.sql.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
@Entity(name = "nextId")
public class NextIdEntity {
    @Id
    @NotBlank
    private String projectKey;
    private int nextId;
}
