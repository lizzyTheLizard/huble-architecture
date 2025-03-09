package site.gutschi.humble.spring.billing.usecases;


import org.mockito.Mockito;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import site.gutschi.humble.spring.billing.ports.BillRepository;
import site.gutschi.humble.spring.billing.ports.BillingPeriodRepository;
import site.gutschi.humble.spring.billing.ports.CostCenterRepository;
import site.gutschi.humble.spring.tasks.api.GetTasksApi;
import site.gutschi.humble.spring.users.api.GetProjectApi;

@Configuration
public class MockBeans {
    @Bean
    GetProjectApi getProjectUseCase() {
        return Mockito.mock(GetProjectApi.class);
    }

    @Bean
    GetTasksApi getTasksUseCase() {
        return Mockito.mock(GetTasksApi.class);
    }

    @Bean
    BillRepository billRepository() {
        return Mockito.mock(BillRepository.class);
    }

    @Bean
    BillingPeriodRepository billingPeriodRepository() {
        return Mockito.mock(BillingPeriodRepository.class);
    }

    @Bean
    CostCenterRepository costCenterRepository() {
        return Mockito.mock(CostCenterRepository.class);
    }
}
