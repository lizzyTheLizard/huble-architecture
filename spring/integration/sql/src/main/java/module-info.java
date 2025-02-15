module site.gutschi.humble.spring.integration.sql {
    requires lombok;
    //noinspection Java9RedundantRequiresStatement used for lombok
    requires org.slf4j;
    requires spring.context;
    requires site.gutschi.humble.spring.users;
    requires site.gutschi.humble.spring.tasks;
    requires spring.data.jpa;
    requires jakarta.persistence;
    requires jakarta.validation;
    requires spring.boot.autoconfigure;
}