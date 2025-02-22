package site.gutschi.humble.spring.integration.sql;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import site.gutschi.humble.spring.billing.model.CostCenter;
import site.gutschi.humble.spring.billing.ports.CostCenterRepository;
import site.gutschi.humble.spring.integration.sql.entity.CostCenterEntity;
import site.gutschi.humble.spring.integration.sql.entity.NextIdEntity;
import site.gutschi.humble.spring.integration.sql.repo.CostCenterEntityRepository;
import site.gutschi.humble.spring.integration.sql.repo.NextIdEntityRepository;
import site.gutschi.humble.spring.integration.sql.repo.ProjectEntityRepository;
import site.gutschi.humble.spring.users.model.Project;

import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
//TODO: Testing
public class JpaCostCenterRepository implements CostCenterRepository {
    private final NextIdEntityRepository nextIdEntityRepository;
    private final CostCenterEntityRepository costCenterEntityRepository;
    private final ProjectEntityRepository projectEntityRepository;

    @Override
    public Optional<CostCenter> findById(int id) {
        return costCenterEntityRepository.findById(id)
                .map(CostCenterEntity::toModel);
    }

    @Override
    public void save(CostCenter costCenter) {
        final var entity = CostCenterEntity.fromModel(costCenter);
        costCenterEntityRepository.save(entity);
    }

    @Override
    public Set<CostCenter> findAll() {
        return costCenterEntityRepository.findAll().stream()
                .map(CostCenterEntity::toModel)
                .collect(Collectors.toSet());
    }

    @Override
    public Optional<CostCenter> findByProject(Project project) {
        final var projectEntity = projectEntityRepository.getReferenceById(project.getKey());
        return costCenterEntityRepository.findByProject(projectEntity)
                .map(CostCenterEntity::toModel);
    }

    @Override
    public int getNextId() {
        final var name = "COSTCENTER";
        final int nextId = nextIdEntityRepository.findById(name)
                .map(NextIdEntity::getNextId)
                .orElse(1);
        final var newEntity = new NextIdEntity();
        newEntity.setName(name);
        newEntity.setNextId(nextId + 1);
        nextIdEntityRepository.save(newEntity);
        return nextId;
    }
}
