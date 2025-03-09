package site.gutschi.humble.spring.users.api;


import site.gutschi.humble.spring.users.model.User;

/**
 * API to get the current user.
 */
public interface CurrentUserApi {
    /**
     * Get the current user email.
     *
     * @return the email of the current user
     */
    User getCurrentUser();

    /**
     * Is the current user a system admin?
     *
     * @return true if the current user is a system admin
     */
    boolean isSystemAdmin();
}
