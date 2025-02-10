package site.gutschi.humble.spring.common.api;


public interface CurrentUserApi {
    String currentEmail();

    boolean isSystemAdmin();
}
