package site.gutschi.humble.spring.billing.implementation;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import site.gutschi.humble.spring.billing.model.Bill;
import site.gutschi.humble.spring.billing.model.BillingPeriod;
import site.gutschi.humble.spring.billing.model.ProjectBill;
import site.gutschi.humble.spring.billing.ports.BillRepository;
import site.gutschi.humble.spring.billing.ports.BillingPeriodRepository;
import site.gutschi.humble.spring.billing.ports.CostCenterRepository;
import site.gutschi.humble.spring.billing.usecases.UpdateBillsUseCase;
import site.gutschi.humble.spring.common.helper.TimeHelper;
import site.gutschi.humble.spring.tasks.model.Task;
import site.gutschi.humble.spring.tasks.model.TaskHistoryType;
import site.gutschi.humble.spring.tasks.usecases.GetTasksUseCase;
import site.gutschi.humble.spring.users.model.Project;
import site.gutschi.humble.spring.users.model.ProjectHistoryType;

import java.math.BigDecimal;
import java.math.MathContext;
import java.time.LocalDate;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
@Slf4j
public class UpdateBillingService implements UpdateBillsUseCase {
    private final BillRepository billRepository;
    private final CanAccessPolicy canAccessPolicy;
    private final CostCenterRepository costCenterRepository;
    private final GetTasksUseCase getTasksUseCase;
    private final BillingConfiguration billingConfiguration;
    private final BillingPeriodRepository billingPeriodRepository;

    @Override
    public void updateBills() {
        canAccessPolicy.ensureCanAccessBilling();
        final var firstStart = getFirstStart();
        final var lastStartExclusive = getLastStartExclusive();
        final var today = TimeHelper.today();
        final var dueDate = today.plusDays(30);
        for (var start = firstStart; start.isBefore(lastStartExclusive); start = start.plusMonths(1)) {
            for (var costCenter : costCenterRepository.findAll()) {
                final var finalStart = start;
                final var projectBills = costCenter.getProjects().stream()
                        .filter(p -> wasActive(p, finalStart))
                        .map(p -> createProjectBill(p, finalStart))
                        .collect(Collectors.toSet());
                final var id = billRepository.nextId();
                final var bill = new Bill(id, costCenter, start, dueDate, today, projectBills);
                billRepository.save(bill);
            }
            final var newBillingPeriod = new BillingPeriod(start, dueDate, today);
            billingPeriodRepository.save(newBillingPeriod);
        }
    }

    private LocalDate getFirstStart() {
        return billingPeriodRepository.getLatestBillingPeriod()
                .map(BillingPeriod::getBillingPeriodStart)
                .orElse(TimeHelper.today().withDayOfMonth(1).minusMonths(1));

    }

    private LocalDate getLastStartExclusive() {
        return TimeHelper.today().withDayOfMonth(1);
    }

    private boolean wasActive(Project project, LocalDate start) {
        if (project.isActive()) return true;
        final var end = start.plusMonths(1);
        return project.getHistoryEntries().stream()
                .filter(e -> !TimeHelper.dateOf(e.timestamp()).isBefore(start))
                .filter(e -> !TimeHelper.dateOf(e.timestamp()).isAfter(end))
                .anyMatch(e -> e.type() == ProjectHistoryType.ACTIVATE_CHANGED);
    }

    private ProjectBill createProjectBill(Project project, LocalDate start) {
        final var totalTasks = countTotalTasks(project, start);
        final var createdTasks = countCreatedTasks(project, start);
        final var costTasks = billingConfiguration.getCostsPerTask().multiply(BigDecimal.valueOf(totalTasks));
        final var costCreated = billingConfiguration.getCostsPerCreatedTask().multiply(BigDecimal.valueOf(createdTasks));
        final var amount = costCreated.add(costTasks).round(new MathContext(2));
        return new ProjectBill(project, amount, totalTasks, createdTasks);
    }

    private int countTotalTasks(Project project, LocalDate start) {
        final var end = start.plusMonths(1);
        return (int) getTasksUseCase.getTasksForProject(project.getKey()).tasks().stream()
                .filter(t -> !getCreatedDate(t).isAfter(end))
                .filter(t -> !start.isAfter(getDeletedDate(t)))
                .count();
    }

    private int countCreatedTasks(Project project, LocalDate start) {
        final var end = start.plusMonths(1);
        return (int) getTasksUseCase.getTasksForProject(project.getKey()).tasks().stream()
                .filter(t -> !getCreatedDate(t).isAfter(end))
                .filter(t -> !start.isAfter(getCreatedDate(t)))
                .count();
    }

    private LocalDate getCreatedDate(Task t) {
        final var createdEntry = t.getHistoryEntries().stream()
                .filter(e -> e.type().equals(TaskHistoryType.CREATED))
                .findFirst();
        if (createdEntry.isEmpty()) {
            log.warn("Task '{}' has no CREATED date", t.getKey());
            return LocalDate.MIN;
        }
        return TimeHelper.dateOf(createdEntry.get().timestamp());
    }

    private LocalDate getDeletedDate(Task t) {
        return t.getHistoryEntries().stream()
                .filter(e -> e.type().equals(TaskHistoryType.DELETED))
                .map(e -> TimeHelper.dateOf(e.timestamp()))
                .findFirst()
                .orElse(LocalDate.MAX);
    }
}
