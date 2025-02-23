package site.gutschi.humble.spring.integration.inmemory;

import org.springframework.stereotype.Service;
import site.gutschi.humble.spring.billing.model.CostCenter;
import site.gutschi.humble.spring.billing.ports.CostCenterRepository;
import site.gutschi.humble.spring.users.model.Project;

import java.util.Collections;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@Service
public class InMemoryCostCenterRepository implements CostCenterRepository {
    private final Set<CostCenter> costCenters = new HashSet<>();
    private int nextId = 1;

    @Override
    public Optional<CostCenter> findById(int id) {
        return costCenters.stream()
                .filter(c -> c.getId() == id)
                .findFirst();
    }

    @Override
    public CostCenter save(CostCenter costCenter) {
        if (costCenter.getId() != null)
            costCenters.removeIf(c -> c.getId().equals(costCenter.getId()));
        final var result = CostCenter.builder()
                .id(costCenter.getId() != null ? costCenter.getId() : nextId++)
                .name(costCenter.getName())
                .address(costCenter.getAddress())
                .email(costCenter.getEmail())
                .deleted(costCenter.isDeleted())
                .projects(costCenter.getProjects())
                .build();
        costCenters.add(result);
        return result;
    }

    @Override
    public Set<CostCenter> findAll() {
        return Collections.unmodifiableSet(costCenters);
    }

    @Override
    public Optional<CostCenter> findByProject(Project project) {
        return costCenters.stream()
                .filter(c -> c.getProjects().contains(project))
                .findFirst();
    }
}
