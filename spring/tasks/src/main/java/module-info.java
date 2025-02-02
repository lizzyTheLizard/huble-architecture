module site.gutschi.humble.spring.tasks {
    requires lombok;
    requires site.gutschi.humble.spring.common;
    requires spring.users;
    //required be generated lombok code
    //noinspection Java9RedundantRequiresStatement
    requires org.slf4j;
    requires spring.context;
    requires spring.web;
    requires jakarta.validation;
}