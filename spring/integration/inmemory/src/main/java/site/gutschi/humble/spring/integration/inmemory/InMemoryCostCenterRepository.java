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
    private int nextId = 0;

    @Override
    public Optional<CostCenter> findById(int id) {
        return costCenters.stream()
                .filter(c -> c.getId() == id)
                .findFirst();
    }

    @Override
    public void save(CostCenter costCenter) {
        costCenters.removeIf(c -> c.getId() == costCenter.getId());
        costCenters.add(costCenter);
    }

    @Override
    public Set<CostCenter> findAll() {
        return Collections.unmodifiableSet(costCenters);
    }

    @Override
    public int getNextId() {
        final var result = nextId;
        nextId++;
        return result;
    }

    @Override
    public Optional<CostCenter> findByProject(Project project) {
        return costCenters.stream()
                .filter(c -> c.getProjects().contains(project))
                .findFirst();
    }
}
