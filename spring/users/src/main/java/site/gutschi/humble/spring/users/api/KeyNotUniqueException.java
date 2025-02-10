package site.gutschi.humble.spring.users.api;

public class KeyNotUniqueException extends RuntimeException {
    private KeyNotUniqueException(String message) {
        super(message);
    }

    public static KeyNotUniqueException projectKeyAlreadyExists(String projectKey) {
        return new KeyNotUniqueException("Project key '" + projectKey + "' already exists");
    }

    public static KeyNotUniqueException userMailAlreadyExists(String mail) {
        return new KeyNotUniqueException("User with mail '" + mail + "' already exists");
    }

}
