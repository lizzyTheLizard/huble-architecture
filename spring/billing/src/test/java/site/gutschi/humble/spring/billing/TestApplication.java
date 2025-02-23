package site.gutschi.humble.spring.billing;


import org.mockito.Mockito;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import site.gutschi.humble.spring.billing.ports.BillRepository;
import site.gutschi.humble.spring.billing.ports.BillingPeriodRepository;
import site.gutschi.humble.spring.billing.ports.CostCenterRepository;
import site.gutschi.humble.spring.tasks.usecases.GetTasksUseCase;
import site.gutschi.humble.spring.users.usecases.GetProjectUseCase;

@SpringBootConfiguration
@ComponentScan
public class TestApplication {
    @Bean
    GetProjectUseCase getProjectUseCase() {
        return Mockito.mock(GetProjectUseCase.class);
    }

    @Bean
    GetTasksUseCase getTasksUseCase() {
        return Mockito.mock(GetTasksUseCase.class);
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
