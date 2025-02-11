package site.gutschi.humble.spring.users.api;

public interface EditProjectUseCase {
    void editProject(EditProjectRequest request);

    void assignUser(AssignUserRequest request);
}
