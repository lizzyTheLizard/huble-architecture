module site.gutschi.humble.spring.tasks {
    requires jakarta.validation;
    //noinspection Java9RedundantRequiresStatement used for lombok
    requires org.slf4j;
    requires spring.context;
    requires site.gutschi.humble.spring.users;
    requires site.gutschi.humble.spring.common;
    requires static lombok;
    exports site.gutschi.humble.spring.tasks.api;
    exports site.gutschi.humble.spring.tasks.ports;
    exports site.gutschi.humble.spring.tasks.model;
}