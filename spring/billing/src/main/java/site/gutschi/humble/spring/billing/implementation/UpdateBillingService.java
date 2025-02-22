package site.gutschi.humble.spring.billing.implementation;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import site.gutschi.humble.spring.billing.model.Bill;
import site.gutschi.humble.spring.billing.model.BillingPeriod;
import site.gutschi.humble.spring.billing.model.CostCenter;
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
    private final CanAccessBillingPolicy canAccessBillingPolicy;
    private final CostCenterRepository costCenterRepository;
    private final GetTasksUseCase getTasksUseCase;
    private final BillingConfiguration billingConfiguration;
    private final BillingPeriodRepository billingPeriodRepository;

    @Override
    public void updateBills() {
        canAccessBillingPolicy.ensureCanAccessBilling();
        final var lastStartExclusive = getLastStartExclusive();
        for (var start = getFirstStart(); start.isBefore(lastStartExclusive); start = start.plusMonths(1)) {
            final var billingPeriod = createNewBillingPeriod(start);
            for (var costCenter : costCenterRepository.findAll()) {
                createNewBill(costCenter, billingPeriod);
            }
        }
    }

    private LocalDate getFirstStart() {
        return billingPeriodRepository.getLatestBillingPeriod()
                .map(BillingPeriod::start)
                .map(start -> start.plusMonths(1))
                .orElse(TimeHelper.today().withDayOfMonth(1).minusMonths(1));
    }

    private LocalDate getLastStartExclusive() {
        return TimeHelper.today().withDayOfMonth(1);
    }

    private BillingPeriod createNewBillingPeriod(LocalDate start) {
        final var today = TimeHelper.today();
        final var dueDate = today.plusDays(30);
        final var newBillingPeriod = new BillingPeriod(null, start, dueDate, today);
        return billingPeriodRepository.save(newBillingPeriod);
    }

    private void createNewBill(CostCenter costCenter, BillingPeriod billingPeriod) {
        final var projectBills = costCenter.getProjects().stream()
                .filter(p -> wasActive(p, billingPeriod))
                .map(p -> createProjectBill(p, billingPeriod))
                .collect(Collectors.toSet());
        final var bill = new Bill(null, costCenter, billingPeriod, projectBills);
        billRepository.save(bill);
    }

    private boolean wasActive(Project project, BillingPeriod billingPeriod) {
        final var createdDate = getCreatedDate(project);
        if (!billingPeriod.isInOrBefore(createdDate)) return false;
        if (project.isActive()) return true;
        return project.getHistoryEntries().stream()
                .filter(e -> billingPeriod.isIn(TimeHelper.dateOf(e.timestamp())))
                .anyMatch(e -> e.type() == ProjectHistoryType.ACTIVATE_CHANGED);
    }

    private ProjectBill createProjectBill(Project project, BillingPeriod billingPeriod) {
        final var totalTasks = countTotalTasks(project, billingPeriod);
        final var createdTasks = countCreatedTasks(project, billingPeriod);
        final var costTasks = billingConfiguration.getCostsPerTask().multiply(BigDecimal.valueOf(totalTasks));
        final var costCreated = billingConfiguration.getCostsPerCreatedTask().multiply(BigDecimal.valueOf(createdTasks));
        final var amount = costCreated.add(costTasks).round(new MathContext(2));
        return new ProjectBill(project, amount, totalTasks, createdTasks);
    }

    private long countTotalTasks(Project project, BillingPeriod billingPeriod) {
        return getTasksUseCase.getTasksForProject(project.getKey()).tasks().stream()
                .filter(t -> billingPeriod.isInOrBefore(getCreatedDate(t)))
                .filter(t -> !billingPeriod.isIn(getDeletedDate(t)))
                .count();
    }

    private long countCreatedTasks(Project project, BillingPeriod billingPeriod) {
        return getTasksUseCase.getTasksForProject(project.getKey()).tasks().stream()
                .filter(t -> !billingPeriod.isIn(getCreatedDate(t)))
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

    private LocalDate getCreatedDate(Project p) {
        final var createdEntry = p.getHistoryEntries().stream()
                .filter(e -> e.type().equals(ProjectHistoryType.CREATED))
                .findFirst();
        if (createdEntry.isEmpty()) {
            log.warn("Project '{}' has no CREATED date", p.getKey());
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
