package site.gutschi.humble.spring.common.api;


/**
 * API to get the current user.
 */
public interface CurrentUserApi {
    /**
     * Get the current user email.
     *
     * @return the email of the current user
     */
    String currentEmail();

    /**
     * Is the current user a system admin?
     *
     * @return true if the current user is a system admin
     */
    boolean isSystemAdmin();
}
