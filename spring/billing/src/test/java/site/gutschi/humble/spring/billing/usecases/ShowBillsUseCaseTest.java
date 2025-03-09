package site.gutschi.humble.spring.billing.usecases;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import site.gutschi.humble.spring.billing.model.Bill;
import site.gutschi.humble.spring.billing.model.BillingPeriod;
import site.gutschi.humble.spring.billing.model.CostCenter;
import site.gutschi.humble.spring.billing.ports.BillRepository;
import site.gutschi.humble.spring.billing.ports.BillingPeriodRepository;
import site.gutschi.humble.spring.billing.ports.CostCenterRepository;
import site.gutschi.humble.spring.common.exception.NotAllowedException;
import site.gutschi.humble.spring.users.api.CurrentUserApi;
import site.gutschi.humble.spring.users.model.User;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

@SpringBootTest
class ShowBillsUseCaseTest {
    @Autowired
    private ShowBillsUseCase target;

    @MockitoBean
    private CostCenterRepository costCenterRepository;

    @MockitoBean
    private CurrentUserApi currentUserApi;

    @MockitoBean
    private BillingPeriodRepository billingPeriodRepository;

    @MockitoBean
    private BillRepository billRepository;

    private CostCenter costCenter;
    private Bill bill;
    private BillingPeriod billingPeriod;


    @BeforeEach
    void setUp() {
        costCenter = new CostCenter(3, "name", List.of("address"), "old@example.com", false, Set.of());
        billingPeriod = new BillingPeriod(1, LocalDate.MIN, LocalDate.MIN, LocalDate.MIN);
        bill = new Bill(1, costCenter, billingPeriod, Set.of());
        User currentUser = User.builder().email("dev@example.com").name("Hans").build();

        Mockito.when(costCenterRepository.findAll()).thenReturn(Set.of(costCenter));
        Mockito.when(costCenterRepository.findById(costCenter.getId())).thenReturn(Optional.of(costCenter));
        Mockito.when(billingPeriodRepository.findAll()).thenReturn(Set.of(billingPeriod));
        Mockito.when(billingPeriodRepository.findById(billingPeriod.id())).thenReturn(Optional.of(billingPeriod));
        Mockito.when(billRepository.findAllForCostCenter(costCenter)).thenReturn(Set.of(bill));
        Mockito.when(billRepository.findAllForPeriod(billingPeriod)).thenReturn(Set.of(bill));
        Mockito.when(currentUserApi.isSystemAdmin()).thenReturn(true);
        Mockito.when(currentUserApi.getCurrentUser()).thenReturn(currentUser);
    }

    @Test
    void notAllowed() {
        Mockito.when(currentUserApi.isSystemAdmin()).thenReturn(false);

        assertThatExceptionOfType(NotAllowedException.class).isThrownBy(() -> target.getAllBillingPeriods());
        assertThatExceptionOfType(NotAllowedException.class).isThrownBy(() -> target.getAllCostCenters());
        assertThatExceptionOfType(NotAllowedException.class).isThrownBy(() -> target.getAllForCostCenter(costCenter.getId()));
        assertThatExceptionOfType(NotAllowedException.class).isThrownBy(() -> target.getAllForPeriod(billingPeriod.id()));
    }

    @Test
    void getAllBillingPeriods() {
        final var result = target.getAllBillingPeriods();

        assertThat(result).containsExactly(billingPeriod);
    }

    @Test
    void getAllForPeriod() {
        final var result = target.getAllForPeriod(billingPeriod.id());

        assertThat(result).containsExactly(bill);
    }

    @Test
    void getAllForCostCenter() {
        final var result = target.getAllForCostCenter(costCenter.getId());

        assertThat(result).containsExactly(bill);
    }

    @Test
    void getAllCostCenters() {
        final var result = target.getAllCostCenters();

        assertThat(result).containsExactly(costCenter);
    }
}