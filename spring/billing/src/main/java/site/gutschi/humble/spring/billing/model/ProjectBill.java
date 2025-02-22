package site.gutschi.humble.spring.billing.model;

import site.gutschi.humble.spring.users.model.Project;

import java.math.BigDecimal;

/**
 * A bill for a single project in a billing period.
 *
 * @param project              THe project for which the bill is created.
 * @param amount               The amount that is billed for the project.
 * @param totalNonDeletedTasks The amount of tasks existing in the project during the billing period.
 *                             Deleted tasks are not counted.
 * @param createdTasks         Task created during the billing period.
 */
public record ProjectBill(
        Project project,
        BigDecimal amount,
        long totalNonDeletedTasks,
        long createdTasks) {
}
