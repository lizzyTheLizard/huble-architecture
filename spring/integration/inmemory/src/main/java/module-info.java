module site.gutschi.humble.spring.integration.inmemory {
    requires jakarta.annotation;
    requires lombok;
    //noinspection Java9RedundantRequiresStatement used for lombok
    requires org.slf4j;
    requires spring.context;
    requires site.gutschi.humble.spring.common;
    requires site.gutschi.humble.spring.users;
    requires site.gutschi.humble.spring.tasks;
}