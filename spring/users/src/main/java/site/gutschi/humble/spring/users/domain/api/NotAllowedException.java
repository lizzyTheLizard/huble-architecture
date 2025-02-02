package site.gutschi.humble.spring.users.domain.api;

public class NotAllowedException extends RuntimeException {
    private NotAllowedException(String message) {
        super(message);
    }

    public static NotAllowedException notAllowedToManageUser(String currentUserEmail, String otherUserEmail) {
        return new NotAllowedException("User " + currentUserEmail + " is not allowed to manage user " + otherUserEmail);
    }

    public static NotAllowedException notAllowedToReadUser(String currentUserEmail, String otherUserEmail) {
        return new NotAllowedException("User " + currentUserEmail + " is not allowed to read user " + otherUserEmail);
    }

    public static NotAllowedException notAllowedToWriteProject(String currentUserEmail, String projecyKey) {
        return new NotAllowedException("User " + currentUserEmail + " is not allowed to write project " + projecyKey);
    }

    public static NotAllowedException notAllowedToManageProject(String currentUserEmail, String projecyKey) {
        return new NotAllowedException("User " + currentUserEmail + " is not allowed to manage project " + projecyKey);
    }

    public static NotAllowedException notAllowedToReadProject(String currentUserEmail, String projecyKey) {
        return new NotAllowedException("User " + currentUserEmail + " is not allowed to read project " + projecyKey);
    }

    public static NotAllowedException projectNotActive(String projecyKey) {
        return new NotAllowedException("Project " + projecyKey + " is not active any more");
    }
}
