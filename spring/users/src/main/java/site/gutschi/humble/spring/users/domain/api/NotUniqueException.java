package site.gutschi.humble.spring.users.domain.api;

public class NotUniqueException extends RuntimeException{

    private NotUniqueException(String message) {
        super(message);
    }

    public static NotUniqueException keyAlreadyExists(String key) {
        return new NotUniqueException("Project with key " + key + " already exists");
    }

    public static NotUniqueException emailAlreadyExists(String email) {
        return new NotUniqueException("User with email " + email + " already exists");
    }
}
