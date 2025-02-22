package site.gutschi.humble.spring.integration.sql;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import site.gutschi.humble.spring.billing.model.CostCenter;
import site.gutschi.humble.spring.billing.ports.CostCenterRepository;
import site.gutschi.humble.spring.integration.sql.entity.CostCenterEntity;
import site.gutschi.humble.spring.integration.sql.repo.CostCenterEntityRepository;
import site.gutschi.humble.spring.integration.sql.repo.ProjectEntityRepository;
import site.gutschi.humble.spring.users.model.Project;

import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
//TODO BILLING: Test JpaCostCenterRepository
public class JpaCostCenterRepository implements CostCenterRepository {
    private final CostCenterEntityRepository costCenterEntityRepository;
    private final ProjectEntityRepository projectEntityRepository;

    @Override
    public Optional<CostCenter> findById(int id) {
        return costCenterEntityRepository.findById(id)
                .map(CostCenterEntity::toModel);
    }

    @Override
    public CostCenter save(CostCenter costCenter) {
        final var entity = CostCenterEntity.fromModel(costCenter);
        return costCenterEntityRepository.save(entity).toModel();
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
        return costCenterEntityRepository.findByProjects(projectEntity)
                .map(CostCenterEntity::toModel);
    }
}
