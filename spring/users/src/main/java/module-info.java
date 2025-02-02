module spring.users {
    requires lombok;
    requires site.gutschi.humble.spring.common;
    requires spring.context;
    exports site.gutschi.humble.spring.users.domain.api;
    exports site.gutschi.humble.spring.users.model;
}