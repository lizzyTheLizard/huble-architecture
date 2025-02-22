package site.gutschi.humble.spring.billing.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import site.gutschi.humble.spring.users.model.Project;

import java.math.BigDecimal;

@Getter
@RequiredArgsConstructor
//TODO: Document
public class ProjectBill {
    private final Project project;
    private final BigDecimal amount;
    private final int totalNonDeletedTasks;
    private final int createdTasks;
}
