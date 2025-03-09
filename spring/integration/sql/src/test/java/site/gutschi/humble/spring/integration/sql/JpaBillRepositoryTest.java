package site.gutschi.humble.spring.integration.sql;

import org.assertj.core.internal.Failures;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import site.gutschi.humble.spring.billing.model.Bill;
import site.gutschi.humble.spring.billing.model.BillingPeriod;
import site.gutschi.humble.spring.billing.model.CostCenter;
import site.gutschi.humble.spring.billing.model.ProjectBill;
import site.gutschi.humble.spring.billing.ports.BillRepository;
import site.gutschi.humble.spring.billing.ports.BillingPeriodRepository;
import site.gutschi.humble.spring.billing.ports.CostCenterRepository;
import site.gutschi.humble.spring.common.test.PostgresContainer;
import site.gutschi.humble.spring.users.model.Project;
import site.gutschi.humble.spring.users.model.User;
import site.gutschi.humble.spring.users.ports.ProjectRepository;
import site.gutschi.humble.spring.users.ports.UserRepository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Testcontainers
class JpaBillRepositoryTest {
    @Container
    static final PostgresContainer container = new PostgresContainer();
    @Autowired
    private BillRepository billRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private CostCenterRepository costCenterRepository;
    @Autowired
    private ProjectRepository projectRepository;
    @Autowired
    private BillingPeriodRepository billingPeriodRepository;

    @DynamicPropertySource
    static void registerPgProperties(DynamicPropertyRegistry registry) {
        container.registerProperties(registry);
    }

    private static Consumer<Bill> isEqualTo(Bill bill) {
        return result -> {
            assertThat(result.id()).isNotNull();
            assertThat(result.costCenter().getId()).isEqualTo(bill.costCenter().getId());
            assertThat(result.billingPeriod().id()).isEqualTo(bill.billingPeriod().id());
            assertThat(result.projectBills()).hasSize(bill.projectBills().size());
            for (var resultProjectBill : result.projectBills()) {
                final var projectBill = bill.projectBills()
                        .stream()
                        .filter(rpb -> rpb.project().getKey().equals(resultProjectBill.project().getKey()))
                        .findFirst()
                        .orElseThrow(() -> Failures.instance().failure("No bill for project '" + resultProjectBill.project().getKey() + "' found in " + bill.projectBills()));
                assertThat(resultProjectBill.totalNonDeletedTasks()).isEqualTo(projectBill.totalNonDeletedTasks());
                assertThat(resultProjectBill.amount()).isEqualTo(projectBill.amount());
                assertThat(resultProjectBill.createdTasks()).isEqualTo(projectBill.createdTasks());
            }
        };
    }

    @Test
    void saveAndReload() {
        final var user = User.builder().email("dev@example.com").name("Hans").build();
        userRepository.save(user);
        final var project = Project.createNew("TEST", "Test Project", user);
        projectRepository.save(project);
        final var costCenter = costCenterRepository.save(CostCenter.create("Cost Center", List.of("A11", "A2"), "cc@example.com"));
        final var billingPeriod = billingPeriodRepository.save(BillingPeriod.create(LocalDate.of(2020, 1, 1)));
        final var projectBill = new ProjectBill(project, BigDecimal.valueOf(112, 2), 1, 1);
        final var bill = new Bill(null, costCenter, billingPeriod, Set.of(projectBill));

        billRepository.save(bill);

        final var pResult = billRepository.findAllForPeriod(billingPeriod);
        assertThat(pResult).singleElement().satisfies(isEqualTo(bill));

        final var ccResult = billRepository.findAllForCostCenter(costCenter);
        assertThat(ccResult).singleElement().satisfies(isEqualTo(bill));
    }
}