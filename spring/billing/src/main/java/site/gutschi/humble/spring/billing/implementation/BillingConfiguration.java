package site.gutschi.humble.spring.billing.implementation;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import java.math.BigDecimal;


@Configuration
@Data
public class BillingConfiguration {
    @Value("${site.gutschi.humble.spring.billing.costsPerTask}")
    private BigDecimal costsPerTask;
    @Value("${site.gutschi.humble.spring.billing.costsPerCreatedTask}")
    private BigDecimal costsPerCreatedTask;
}
