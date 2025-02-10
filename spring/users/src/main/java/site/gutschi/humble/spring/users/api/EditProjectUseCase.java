package site.gutschi.humble.spring.users.api;

//TODO: Create test cases
public interface EditProjectUseCase {
    void editProject(EditProjectRequest request);

    void assignUser(AssignUserRequest request);
}
