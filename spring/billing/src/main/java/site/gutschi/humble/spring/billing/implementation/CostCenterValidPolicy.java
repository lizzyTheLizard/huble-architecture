package site.gutschi.humble.spring.billing.implementation;

import lombok.RequiredArgsConstructor;
import org.apache.commons.validator.routines.EmailValidator;
import org.springframework.stereotype.Service;
import site.gutschi.humble.spring.billing.model.CostCenter;
import site.gutschi.humble.spring.common.exception.InvalidInputException;

@Service
@RequiredArgsConstructor
public class CostCenterValidPolicy {
    public void ensureCostCenterValid(CostCenter costCenter) {
        if (costCenter.getAddress() == null || costCenter.getAddress().isEmpty())
            throw new InvalidInputException("Cost center must have an address");
        if (costCenter.getProjects() == null)
            throw new InvalidInputException("Cost center must have projects");
        if (costCenter.getName() == null || costCenter.getName().isBlank())
            throw new InvalidInputException("Cost center name must not be empty");
        if (costCenter.getEmail() == null || costCenter.getEmail().isBlank())
            throw new InvalidInputException("Cost center email must not be empty");
        if (!EmailValidator.getInstance().isValid(costCenter.getEmail()))
            throw new InvalidInputException("Cost center email must be valid");
    }
}
