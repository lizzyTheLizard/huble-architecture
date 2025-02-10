module site.gutschi.humble.spring.users {
    requires lombok;
    requires spring.context;
    //noinspection Java9RedundantRequiresStatement used for lombok
    requires org.slf4j;
    requires site.gutschi.humble.spring.common;
    exports site.gutschi.humble.spring.users.api;
    exports site.gutschi.humble.spring.users.ports;
    exports site.gutschi.humble.spring.users.model;
}