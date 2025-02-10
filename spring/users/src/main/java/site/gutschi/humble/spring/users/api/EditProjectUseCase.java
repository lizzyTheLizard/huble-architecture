package site.gutschi.humble.spring.users.api;

//TODO: Create test cases and remove this warning
@SuppressWarnings("unused")
public interface EditProjectUseCase {
    void editProject(EditProjectRequest request);

    void assignUser(AssignUserRequest request);
}
