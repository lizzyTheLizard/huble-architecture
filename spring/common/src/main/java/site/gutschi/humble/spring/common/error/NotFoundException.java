package site.gutschi.humble.spring.common.error;

public class NotFoundException extends RuntimeException {

    public NotFoundException(String message) {
        super(message);
    }

    public static NotFoundException projectNotFound(String projectKey) {
        return new NotFoundException("Project '" + projectKey + "' could not found");
    }

    public static NotFoundException taskNotFound(String taskKey) {
        return new NotFoundException("Task '" + taskKey + "' could not found");
    }


}
