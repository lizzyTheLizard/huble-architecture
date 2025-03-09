package site.gutschi.humble.spring.integration.sql;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import site.gutschi.humble.spring.billing.model.CostCenter;
import site.gutschi.humble.spring.billing.ports.CostCenterRepository;
import site.gutschi.humble.spring.integration.sql.entity.CostCenterEntity;
import site.gutschi.humble.spring.integration.sql.repo.CostCenterEntityRepository;
import site.gutschi.humble.spring.integration.sql.repo.ProjectEntityRepository;
import site.gutschi.humble.spring.users.model.Project;

import java.util.Collections;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class JpaCostCenterRepository implements CostCenterRepository {
    private final CostCenterEntityRepository costCenterEntityRepository;
    private final ProjectEntityRepository projectEntityRepository;
    private final JpaProjectRepository jpaProjectRepository;

    @Override
    public Optional<CostCenter> findById(int id) {
        return costCenterEntityRepository.findById(id)
                .map(this::toModel);
    }

    @Override
    public CostCenter save(CostCenter costCenter) {
        final var entity = fromModel(costCenter);
        return toModel(costCenterEntityRepository.save(entity));
    }

    @Override
    public Set<CostCenter> findAll() {
        return costCenterEntityRepository.findAll().stream()
                .map(this::toModel)
                .collect(Collectors.toSet());
    }

    @Override
    public Optional<CostCenter> findByProject(Project project) {
        final var projectEntity = projectEntityRepository.getReferenceById(project.getKey());
        return costCenterEntityRepository.findByProjects(projectEntity)
                .map(this::toModel);
    }

    public CostCenter toModel(CostCenterEntity entity) {
        final var address = Collections.unmodifiableList(entity.getAddress());
        final var projects = entity.getProjects().stream()
                .map(jpaProjectRepository::toModel)
                .collect(Collectors.toSet());
        return CostCenter.builder()
                .id(entity.getId())
                .name(entity.getName())
                .address(address)
                .email(entity.getEmail())
                .deleted(entity.isDeleted())
                .projects(projects)
                .build();
    }

    public CostCenterEntity fromModel(CostCenter costCenter) {
        final var entity = new CostCenterEntity();
        entity.setId(costCenter.getId());
        entity.setDeleted(costCenter.isDeleted());
        entity.setEmail(costCenter.getEmail());
        entity.setName(costCenter.getName());
        entity.setAddress(costCenter.getAddress());
        entity.setProjects(costCenter.getProjects().stream()
                .map(p -> projectEntityRepository.getReferenceById(p.getKey()))
                .collect(Collectors.toSet()));
        return entity;
    }

}
