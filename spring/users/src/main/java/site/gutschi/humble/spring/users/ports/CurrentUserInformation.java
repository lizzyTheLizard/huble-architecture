package site.gutschi.humble.spring.users.ports;

import site.gutschi.humble.spring.users.model.User;

public interface CurrentUserInformation {
    User getCurrentUser();

    boolean isSystemAdmin();
}
