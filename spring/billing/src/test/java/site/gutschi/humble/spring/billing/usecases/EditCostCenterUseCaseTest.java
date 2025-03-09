package site.gutschi.humble.spring.billing.usecases;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import site.gutschi.humble.spring.billing.model.CostCenter;
import site.gutschi.humble.spring.billing.ports.CostCenterRepository;
import site.gutschi.humble.spring.common.exception.InvalidInputException;
import site.gutschi.humble.spring.common.exception.NotAllowedException;
import site.gutschi.humble.spring.common.exception.NotFoundException;
import site.gutschi.humble.spring.users.api.CurrentUserApi;
import site.gutschi.humble.spring.users.api.GetProjectApi;
import site.gutschi.humble.spring.users.model.Project;
import site.gutschi.humble.spring.users.model.User;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

@SpringBootTest
class EditCostCenterUseCaseTest {
    @Autowired
    private EditCostCenterUseCase target;

    @MockitoBean
    private CostCenterRepository costCenterRepository;

    @MockitoBean
    private CurrentUserApi currentUserApi;

    @MockitoBean
    private GetProjectApi getProjectApi;

    private CostCenter costCenter;

    @BeforeEach
    void setUp() {
        final var currentUser = User.builder().email("dev@example.com").name("Hans").build();
        costCenter = new CostCenter(3, "name", List.of("address"), "old@example.com", false, Set.of());
        Mockito.when(costCenterRepository.findById(costCenter.getId())).thenReturn(Optional.of(costCenter));
        Mockito.when(currentUserApi.isSystemAdmin()).thenReturn(true);
        Mockito.when(currentUserApi.getCurrentUser()).thenReturn(currentUser);
    }

    @Nested
    class EditCostCenter {
        private EditCostCenterUseCase.EditCostCenterRequest request;

        @BeforeEach
        void setUp() {
            request = new EditCostCenterUseCase.EditCostCenterRequest(costCenter.getId(), List.of("address2"), "new name", "new@example.com");
        }

        @Test
        void notAllowed() {
            Mockito.when(currentUserApi.isSystemAdmin()).thenReturn(false);

            assertThatExceptionOfType(NotAllowedException.class).isThrownBy(() -> target.editCostCenter(request));

            Mockito.verify(costCenterRepository, Mockito.never()).save(costCenter);
        }

        @Test
        void notFound() {
            Mockito.when(costCenterRepository.findById(costCenter.getId())).thenReturn(Optional.empty());

            assertThatExceptionOfType(NotFoundException.class).isThrownBy(() -> target.editCostCenter(request));

            Mockito.verify(costCenterRepository, Mockito.never()).save(costCenter);
        }

        @Test
        void invalidInput() {
            request = new EditCostCenterUseCase.EditCostCenterRequest(costCenter.getId(), List.of("address2"), null, "new email");

            assertThatExceptionOfType(InvalidInputException.class).isThrownBy(() -> target.editCostCenter(request));

            Mockito.verify(costCenterRepository, Mockito.never()).save(costCenter);
        }

        @Test
        void edit() {
            target.editCostCenter(request);

            assertThat(costCenter.getAddress()).isEqualTo(request.address());
            assertThat(costCenter.getName()).isEqualTo(request.name());
            assertThat(costCenter.getEmail()).isEqualTo(request.email());
            Mockito.verify(costCenterRepository, Mockito.times(1)).save(costCenter);
        }
    }

    @Nested
    class CreateCostCenter {
        private EditCostCenterUseCase.CreateCostCenterRequest request;

        @BeforeEach
        void setUp() {
            request = new EditCostCenterUseCase.CreateCostCenterRequest(List.of("address2"), "new name", "new@example.com");
            Mockito.when(costCenterRepository.save(Mockito.any())).thenAnswer(invocation -> invocation.getArgument(0));
        }

        @Test
        void notAllowed() {
            Mockito.when(currentUserApi.isSystemAdmin()).thenReturn(false);

            assertThatExceptionOfType(NotAllowedException.class).isThrownBy(() -> target.createCostCenter(request));

            Mockito.verify(costCenterRepository, Mockito.never()).save(costCenter);
        }

        @Test
        void invalidInput() {
            request = new EditCostCenterUseCase.CreateCostCenterRequest(List.of("address2"), null, "new email");

            assertThatExceptionOfType(InvalidInputException.class).isThrownBy(() -> target.createCostCenter(request));

            Mockito.verify(costCenterRepository, Mockito.never()).save(costCenter);
        }

        @Test
        void create() {
            final var result = target.createCostCenter(request);

            assertThat(result.getAddress()).isEqualTo(request.address());
            assertThat(result.getName()).isEqualTo(request.name());
            assertThat(result.getEmail()).isEqualTo(request.email());
            Mockito.verify(costCenterRepository, Mockito.times(1)).save(result);
        }
    }

    @Nested
    class DeleteCostCenter {
        private EditCostCenterUseCase.DeleteCostCenterRequest request;

        @BeforeEach
        void setUp() {
            request = new EditCostCenterUseCase.DeleteCostCenterRequest(costCenter.getId());
        }

        @Test
        void notAllowed() {
            Mockito.when(currentUserApi.isSystemAdmin()).thenReturn(false);

            assertThatExceptionOfType(NotAllowedException.class).isThrownBy(() -> target.deleteCostCenter(request));

            Mockito.verify(costCenterRepository, Mockito.never()).save(costCenter);
        }

        @Test
        void notFound() {
            Mockito.when(costCenterRepository.findById(costCenter.getId())).thenReturn(Optional.empty());

            assertThatExceptionOfType(NotFoundException.class).isThrownBy(() -> target.deleteCostCenter(request));

            Mockito.verify(costCenterRepository, Mockito.never()).save(costCenter);
        }

        @Test
        void delete() {
            target.deleteCostCenter(request);

            assertThat(costCenter.isDeleted()).isEqualTo(true);
            Mockito.verify(costCenterRepository, Mockito.times(1)).save(costCenter);
        }
    }

    @Nested
    class AssignProjectToCostCenter {
        private EditCostCenterUseCase.AssignCostCenterToUserRequest request;
        private Project project;

        @BeforeEach
        void setUp() {
            final var owner = User.builder().email("dev@example.com").email("Hans").build();
            project = Project.createNew("key", "name", owner);
            costCenter.addProject(project);
            request = new EditCostCenterUseCase.AssignCostCenterToUserRequest(costCenter.getId(), project.getKey());
            Mockito.when(getProjectApi.getProject(project.getKey())).thenReturn(project);
        }

        @Test
        void notAllowed() {
            Mockito.when(currentUserApi.isSystemAdmin()).thenReturn(false);

            assertThatExceptionOfType(NotAllowedException.class).isThrownBy(() -> target.assignProjectToCostCenter(request));

            Mockito.verify(costCenterRepository, Mockito.never()).save(costCenter);
        }

        @Test
        void costCenterNotFound() {
            Mockito.when(costCenterRepository.findById(costCenter.getId())).thenReturn(Optional.empty());

            assertThatExceptionOfType(NotFoundException.class).isThrownBy(() -> target.assignProjectToCostCenter(request));

            Mockito.verify(costCenterRepository, Mockito.never()).save(costCenter);
        }

        @Test
        void edit() {
            target.assignProjectToCostCenter(request);

            assertThat(costCenter.getProjects()).singleElement().isEqualTo(project);
            Mockito.verify(costCenterRepository, Mockito.times(1)).save(costCenter);
        }

    }

    @Nested
    class UnassignProjectFromCostCenter {
        private Project project;

        @BeforeEach
        void setUp() {
            final var owner = User.builder().email("dev@example.com").email("Hans").build();
            project = Project.createNew("key", "name", owner);
            costCenter.addProject(project);
            Mockito.when(getProjectApi.getProject(project.getKey())).thenReturn(project);
            Mockito.when(costCenterRepository.findByProject(project)).thenReturn(Optional.of(costCenter));
        }

        @Test
        void notAllowed() {
            Mockito.when(currentUserApi.isSystemAdmin()).thenReturn(false);

            assertThatExceptionOfType(NotAllowedException.class).isThrownBy(() -> target.unassignProjectFromCostCenter(project.getKey()));

            Mockito.verify(costCenterRepository, Mockito.never()).save(costCenter);
        }

        @Test
        void notFound() {
            Mockito.when(costCenterRepository.findByProject(project)).thenReturn(Optional.empty());

            target.unassignProjectFromCostCenter(project.getKey());

            Mockito.verify(costCenterRepository, Mockito.never()).save(costCenter);
        }

        @Test
        void unassign() {
            target.unassignProjectFromCostCenter(project.getKey());

            assertThat(costCenter.getProjects()).isEmpty();
            Mockito.verify(costCenterRepository, Mockito.times(1)).save(costCenter);
        }
    }
}