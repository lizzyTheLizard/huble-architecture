package site.gutschi.humble.spring.integration.sql;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import site.gutschi.humble.spring.billing.model.BillingPeriod;
import site.gutschi.humble.spring.billing.ports.BillingPeriodRepository;
import site.gutschi.humble.spring.common.test.PostgresContainer;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Testcontainers
class JpaBillingPeriodRepositoryTest {
    @Container
    static final PostgresContainer container = new PostgresContainer();
    @Autowired
    private BillingPeriodRepository billingPeriodRepository;

    @DynamicPropertySource
    static void registerPgProperties(DynamicPropertyRegistry registry) {
        container.registerProperties(registry);
    }

    @Test
    void save() {
        final var billingPeriod = BillingPeriod.create(LocalDate.now().withDayOfMonth(1).minusDays(1));

        final var result = billingPeriodRepository.save(billingPeriod);
        assertThat(result.id()).isNotNull();
        assertThat(result.dueDate()).isEqualTo(billingPeriod.dueDate());
        assertThat(result.createdDate()).isEqualTo(billingPeriod.createdDate());
        assertThat(result.start()).isEqualTo(billingPeriod.start());

        final var result2 = billingPeriodRepository.findById(result.id());
        assertThat(result2).contains(result);
    }

    @Test
    void getLatestBillingPeriod() {
        final var billingPeriod1 = BillingPeriod.create(LocalDate.now().plusYears(1).withDayOfMonth(1).minusMonths(2));
        final var result1 = billingPeriodRepository.save(billingPeriod1);
        assertThat(billingPeriodRepository.getLatestBillingPeriod()).contains(result1);

        final var billingPeriod2 = BillingPeriod.create(LocalDate.now().plusYears(1).withDayOfMonth(1).minusMonths(3));
        billingPeriodRepository.save(billingPeriod2);
        assertThat(billingPeriodRepository.getLatestBillingPeriod()).contains(result1);

        final var billingPeriod3 = BillingPeriod.create(LocalDate.now().plusYears(1).withDayOfMonth(1).minusMonths(1));
        final var result3 = billingPeriodRepository.save(billingPeriod3);
        assertThat(billingPeriodRepository.getLatestBillingPeriod()).contains(result3);
    }

    @Test
    void findAll() {
        final var billingPeriod1 = BillingPeriod.create(LocalDate.now().withDayOfMonth(1).minusMonths(2));
        final var result1 = billingPeriodRepository.save(billingPeriod1);
        assertThat(billingPeriodRepository.findAll()).contains(result1);

        final var billingPeriod2 = BillingPeriod.create(LocalDate.now().withDayOfMonth(1).minusMonths(3));
        final var result2 = billingPeriodRepository.save(billingPeriod2);
        assertThat(billingPeriodRepository.findAll()).contains(result1, result2);

        final var billingPeriod3 = BillingPeriod.create(LocalDate.now().withDayOfMonth(1).minusMonths(1));
        final var result3 = billingPeriodRepository.save(billingPeriod3);
        assertThat(billingPeriodRepository.findAll()).contains(result1, result2, result3);
    }
}