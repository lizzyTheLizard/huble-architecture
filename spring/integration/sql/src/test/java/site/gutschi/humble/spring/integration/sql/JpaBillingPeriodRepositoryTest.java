package site.gutschi.humble.spring.integration.sql;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;
import site.gutschi.humble.spring.billing.model.BillingPeriod;
import site.gutschi.humble.spring.billing.ports.BillingPeriodRepository;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Testcontainers
class JpaBillingPeriodRepositoryTest {
    @Container
    @ServiceConnection
    @SuppressWarnings("resource") // Closed by Spring
    static PostgreSQLContainer<?> container = new PostgreSQLContainer<>(DockerImageName.parse("postgres:16-alpine"))
            .withDatabaseName("test")
            .withUsername("postgres")
            .withPassword("password");

    @Autowired
    private BillingPeriodRepository billingPeriodRepository;

    @Test
    void save() {
        final var billingPeriod = BillingPeriod.create(LocalDate.now().withDayOfMonth(1).minusDays(1));

        final var result = billingPeriodRepository.save(billingPeriod);
        assertThat(result.id()).isNotNull();
        assertThat(result.dueDate()).isEqualTo(billingPeriod.dueDate());
        assertThat(result.createdDate()).isEqualTo(billingPeriod.createdDate());
        assertThat(result.start()).isEqualTo(billingPeriod.start());
    }

    @Test
    void getLatestBillingPeriod() {
        assertThat(billingPeriodRepository.getLatestBillingPeriod()).isEmpty();

        final var billingPeriod1 = BillingPeriod.create(LocalDate.now().withDayOfMonth(1).minusMonths(2));
        final var result1 = billingPeriodRepository.save(billingPeriod1);
        assertThat(billingPeriodRepository.getLatestBillingPeriod()).contains(result1);

        final var billingPeriod2 = BillingPeriod.create(LocalDate.now().withDayOfMonth(1).minusMonths(3));
        billingPeriodRepository.save(billingPeriod2);
        assertThat(billingPeriodRepository.getLatestBillingPeriod()).contains(result1);

        final var billingPeriod3 = BillingPeriod.create(LocalDate.now().withDayOfMonth(1).minusMonths(1));
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