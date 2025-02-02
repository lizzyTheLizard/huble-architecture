package site.gutschi.humble.spring.users.domain.api;

public class NotFoundException extends RuntimeException {
    private NotFoundException(String message) {
        super(message);
    }

    public static NotFoundException userNotFound(String userEmail) {
        return new NotFoundException("User " + userEmail + " does not exist");
    }

    public static NotFoundException userNotVisible(String currentUserEmail, String userEmail) {
        return new NotFoundException("User " + currentUserEmail + " cannot view user " + userEmail);
    }

    public static NotFoundException projectNotFound(String projectKey) {
        return new NotFoundException("Project " + projectKey + " does not exist");
    }

    public static NotFoundException projectNotVisible(String currentUserEmail, String projectKey) {
        return new NotFoundException("User " + currentUserEmail + " cannot view project " + projectKey);
    }

}
