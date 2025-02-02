package site.gutschi.humble.spring.users.domain.api;

public class NotAllowedException extends RuntimeException {
    private NotAllowedException(String message) {
        super(message);
    }

    public static NotAllowedException notAllowedToManageUser(String currentUserEmail, String otherUserEmail) {
        return new NotAllowedException("User " + currentUserEmail + " is not allowed to manage user " + otherUserEmail);
    }

    public static NotAllowedException notAllowedToManageProject(String currentUserEmail, String projectKey) {
        return new NotAllowedException("User " + currentUserEmail + " is not allowed to manage project " + projectKey);
    }
}
