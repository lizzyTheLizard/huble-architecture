module site.gutschi.humble.spring.tasks {
    requires jakarta.validation;
    requires lombok;
    //noinspection Java9RedundantRequiresStatement used for lombok
    requires org.slf4j;
    requires spring.context;
    requires site.gutschi.humble.spring.common;
    requires site.gutschi.humble.spring.users;
    exports site.gutschi.humble.spring.tasks.usecases;
    exports site.gutschi.humble.spring.tasks.ports;
    exports site.gutschi.humble.spring.tasks.model;
}