package site.gutschi.humble.spring.billing.usecases;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import site.gutschi.humble.spring.billing.model.Bill;
import site.gutschi.humble.spring.billing.model.BillingPeriod;
import site.gutschi.humble.spring.billing.model.CostCenter;
import site.gutschi.humble.spring.billing.model.ProjectBill;
import site.gutschi.humble.spring.billing.ports.BillRepository;
import site.gutschi.humble.spring.billing.ports.BillingPeriodRepository;
import site.gutschi.humble.spring.billing.ports.CostCenterRepository;
import site.gutschi.humble.spring.common.api.CurrentUserApi;
import site.gutschi.humble.spring.common.exception.InvalidInputException;
import site.gutschi.humble.spring.common.exception.NotAllowedException;
import site.gutschi.humble.spring.common.helper.TimeHelper;
import site.gutschi.humble.spring.tasks.model.Task;
import site.gutschi.humble.spring.tasks.model.TaskHistoryEntry;
import site.gutschi.humble.spring.tasks.model.TaskHistoryType;
import site.gutschi.humble.spring.tasks.usecases.GetTasksUseCase;
import site.gutschi.humble.spring.users.model.Project;
import site.gutschi.humble.spring.users.model.ProjectHistoryEntry;
import site.gutschi.humble.spring.users.model.ProjectHistoryType;
import site.gutschi.humble.spring.users.model.User;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

@SpringBootTest
class UpdateBillsUseCaseTest {
    @Autowired
    private UpdateBillsUseCase target;

    @MockitoBean
    private CostCenterRepository costCenterRepository;

    @MockitoBean
    private CurrentUserApi currentUserApi;

    @MockitoBean
    private GetTasksUseCase getTasksUseCase;

    @MockitoBean
    private BillingPeriodRepository billingPeriodRepository;

    @MockitoBean
    private BillRepository billRepository;

    private CostCenter costCenter;
    private Project testProject;
    private User currentUser;
    private LocalDate billingPeriodStart;

    @BeforeEach
    void setUp() {
        billingPeriodStart = TimeHelper.today().withDayOfMonth(1).minusMonths(1);
        currentUser = new User("dev@example.com", "Hans");
        costCenter = new CostCenter(3, "name", List.of("address"), "old@example.com", false, Set.of());
        updateTestProject(LocalDate.MIN, List.of(), Set.of());
        Mockito.when(costCenterRepository.findAll()).thenReturn(Set.of(costCenter));
        Mockito.when(currentUserApi.isSystemAdmin()).thenReturn(true);
        Mockito.when(billingPeriodRepository.save(Mockito.any())).thenAnswer(invocation -> invocation.getArgument(0, BillingPeriod.class));
    }

    private Task createTask(LocalDate created, LocalDate deleted) {
        final var builder = Task.builder()
                .id(1)
                .projectKey(testProject.getKey())
                .currentUserApi(currentUserApi)
                .creatorEmail(currentUser.getEmail());
        if (created != null)
            builder.historyEntry(new TaskHistoryEntry(currentUser.getEmail(), TimeHelper.instantOf(created), TaskHistoryType.CREATED, null, null, null));
        if (deleted != null)
            builder.historyEntry(new TaskHistoryEntry(currentUser.getEmail(), TimeHelper.instantOf(deleted), TaskHistoryType.DELETED, null, null, null));
        return builder.build();
    }


    private void updateTestProject(LocalDate created, List<LocalDate> activationChanged, Set<Task> tasks) {
        final var builder = Project.builder()
                .key("TEST")
                .name("Test")
                .active(activationChanged.size() % 2 == 0)
                .currentUserApi(currentUserApi);
        if (created != null)
            builder.historyEntry(new ProjectHistoryEntry(
                    currentUser.getEmail(),
                    TimeHelper.instantOf(created),
                    ProjectHistoryType.CREATED, null, null, null));
        activationChanged.forEach(d ->
                builder.historyEntry(new ProjectHistoryEntry(
                        currentUser.getEmail(),
                        TimeHelper.instantOf(d),
                        ProjectHistoryType.ACTIVATE_CHANGED, null, null, null)));
        costCenter.removeProject(testProject);
        testProject = builder.build();
        costCenter.addProject(testProject);
        Mockito.when(getTasksUseCase.getTasksForProject(testProject.getKey())).thenReturn(tasks);
    }

    @Nested
    class CheckBillingPeriod {

        @Test
        void notAllowed() {
            Mockito.when(currentUserApi.isSystemAdmin()).thenReturn(false);

            assertThatExceptionOfType(NotAllowedException.class).isThrownBy(() -> target.updateBills(billingPeriodStart));

            Mockito.verify(billingPeriodRepository, Mockito.never()).save(Mockito.any());
            Mockito.verify(billRepository, Mockito.never()).save(Mockito.any());
        }

        @Test
        void currentMonth() {
            billingPeriodStart = TimeHelper.today().withDayOfMonth(1);

            assertThatExceptionOfType(InvalidInputException.class).isThrownBy(() -> target.updateBills(billingPeriodStart));

            Mockito.verify(billingPeriodRepository, Mockito.never()).save(Mockito.any());
            Mockito.verify(billRepository, Mockito.never()).save(Mockito.any());
        }

        @Test
        void future() {
            billingPeriodStart = TimeHelper.today().withDayOfMonth(1).plusMonths(1);

            assertThatExceptionOfType(InvalidInputException.class).isThrownBy(() -> target.updateBills(billingPeriodStart));

            Mockito.verify(billingPeriodRepository, Mockito.never()).save(Mockito.any());
            Mockito.verify(billRepository, Mockito.never()).save(Mockito.any());
        }

        @Test
        void notStartOfMonth() {
            billingPeriodStart = billingPeriodStart.withDayOfMonth(3);

            assertThatExceptionOfType(InvalidInputException.class).isThrownBy(() -> target.updateBills(billingPeriodStart));

            Mockito.verify(billingPeriodRepository, Mockito.never()).save(Mockito.any());
            Mockito.verify(billRepository, Mockito.never()).save(Mockito.any());
        }

        @Test
        void created() {
            target.updateBills(billingPeriodStart);

            final var periodCaptor = ArgumentCaptor.forClass(BillingPeriod.class);
            Mockito.verify(billingPeriodRepository, Mockito.times(1)).save(periodCaptor.capture());
            assertThat(periodCaptor.getAllValues()).hasSize(1);
            assertThat(periodCaptor.getValue().id()).isNull();
            assertThat(periodCaptor.getValue().start()).isEqualTo(billingPeriodStart);
            assertThat(periodCaptor.getValue().createdDate()).isEqualTo(TimeHelper.today());
            assertThat(periodCaptor.getValue().dueDate()).isEqualTo(TimeHelper.today().plusDays(30));

            final var billCaptor = ArgumentCaptor.forClass(Bill.class);
            Mockito.verify(billRepository, Mockito.times(1)).save(billCaptor.capture());
            assertThat(billCaptor.getAllValues()).hasSize(1);
            assertThat(billCaptor.getValue().id()).isNull();
            assertThat(billCaptor.getValue().billingPeriod()).isEqualTo(periodCaptor.getValue());
            assertThat(billCaptor.getValue().costCenter()).isEqualTo(costCenter);
            assertThat(billCaptor.getValue().projectBills()).hasSize(1);
        }
    }

    @Nested
    class CheckProjectValid {

        private int getNumberOfProjectsCreated() {
            final var billCaptor = ArgumentCaptor.forClass(Bill.class);
            Mockito.verify(billRepository, Mockito.times(1)).save(billCaptor.capture());
            return billCaptor.getValue().projectBills().size();
        }

        @Test
        void projectDeactivatedBefore() {
            updateTestProject(LocalDate.MIN, List.of(billingPeriodStart.minusDays(1)), Set.of());

            target.updateBills(billingPeriodStart);

            assertThat(getNumberOfProjectsCreated()).isEqualTo(0);
        }

        @Test
        void projectDeactivatedDuring() {
            updateTestProject(LocalDate.MIN, List.of(billingPeriodStart), Set.of());

            target.updateBills(billingPeriodStart);

            assertThat(getNumberOfProjectsCreated()).isEqualTo(1);
        }

        @Test
        void projectReactivatedDuring() {
            updateTestProject(LocalDate.MIN, List.of(billingPeriodStart.minusDays(1), billingPeriodStart.plusMonths(1).minusDays(1)), Set.of());

            target.updateBills(billingPeriodStart);

            assertThat(getNumberOfProjectsCreated()).isEqualTo(1);
        }

        @Test
        void projectCreatedAfter() {
            updateTestProject(billingPeriodStart.plusMonths(1), List.of(), Set.of());

            target.updateBills(billingPeriodStart);

            assertThat(getNumberOfProjectsCreated()).isEqualTo(0);
        }


        @Test
        void projectCreatedIn() {
            updateTestProject(billingPeriodStart.plusMonths(1).minusDays(1), List.of(), Set.of());

            target.updateBills(billingPeriodStart);

            assertThat(getNumberOfProjectsCreated()).isEqualTo(1);
        }
    }

    @Nested
    class CheckTotalTasks {

        private long getNumberOfTasks() {
            final var billCaptor = ArgumentCaptor.forClass(Bill.class);
            Mockito.verify(billRepository, Mockito.times(1)).save(billCaptor.capture());
            final var projectBill = billCaptor.getValue().projectBills().toArray(ProjectBill[]::new)[0];
            return projectBill.totalNonDeletedTasks();
        }

        @Test
        void taskDeletedBefore() {
            final var task = createTask(LocalDate.MIN, billingPeriodStart.minusDays(1));
            updateTestProject(LocalDate.MIN, List.of(), Set.of(task));

            target.updateBills(billingPeriodStart);

            assertThat(getNumberOfTasks()).isEqualTo(0);
        }

        @Test
        void taskDeletedDuring() {
            final var task = createTask(LocalDate.MIN, billingPeriodStart);
            updateTestProject(LocalDate.MIN, List.of(), Set.of(task));

            target.updateBills(billingPeriodStart);

            assertThat(getNumberOfTasks()).isEqualTo(1);
        }

        @Test
        void taskCreatedDuring() {
            final var task = createTask(billingPeriodStart.plusMonths(1).minusDays(1), null);
            updateTestProject(LocalDate.MIN, List.of(), Set.of(task));

            target.updateBills(billingPeriodStart);

            assertThat(getNumberOfTasks()).isEqualTo(1);
        }

        @Test
        void taskCreatedAfter() {
            final var task = createTask(billingPeriodStart.plusMonths(1), null);
            updateTestProject(LocalDate.MIN, List.of(), Set.of(task));

            target.updateBills(billingPeriodStart);

            assertThat(getNumberOfTasks()).isEqualTo(0);
        }
    }

    @Nested
    class CheckCreatedTasks {

        private long getNumberOfCreatedTasks() {
            final var billCaptor = ArgumentCaptor.forClass(Bill.class);
            Mockito.verify(billRepository, Mockito.times(1)).save(billCaptor.capture());
            final var projectBill = billCaptor.getValue().projectBills().toArray(ProjectBill[]::new)[0];
            return projectBill.createdTasks();
        }

        @Test
        void taskCreatedBefore() {
            final var task = createTask(billingPeriodStart.minusDays(1), null);
            updateTestProject(LocalDate.MIN, List.of(), Set.of(task));

            target.updateBills(billingPeriodStart);

            assertThat(getNumberOfCreatedTasks()).isEqualTo(0);
        }

        @Test
        void taskCreatedDuring() {
            final var task = createTask(billingPeriodStart.plusMonths(1).minusDays(1), null);
            updateTestProject(LocalDate.MIN, List.of(), Set.of(task));

            target.updateBills(billingPeriodStart);

            assertThat(getNumberOfCreatedTasks()).isEqualTo(1);
        }

        @Test
        void taskDeletedAgain() {
            final var task = createTask(billingPeriodStart.plusMonths(1).minusDays(2), billingPeriodStart.plusMonths(1).minusDays(1));
            updateTestProject(LocalDate.MIN, List.of(), Set.of(task));

            target.updateBills(billingPeriodStart);

            assertThat(getNumberOfCreatedTasks()).isEqualTo(1);
        }

        @Test
        void taskCreatedAfter() {
            final var task = createTask(billingPeriodStart.plusMonths(1), null);
            updateTestProject(LocalDate.MIN, List.of(), Set.of(task));

            target.updateBills(billingPeriodStart);

            assertThat(getNumberOfCreatedTasks()).isEqualTo(0);
        }
    }
}