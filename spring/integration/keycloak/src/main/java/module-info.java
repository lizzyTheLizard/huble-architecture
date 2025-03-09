module site.gutschi.humble.spring.integration.thymeleaf {
    //noinspection Java9RedundantRequiresStatement used for lombok
    requires org.slf4j;
    requires spring.security.core;
    requires spring.security.config;
    requires spring.security.web;
    requires spring.security.oauth2.core;
    requires site.gutschi.humble.spring.users;
    requires spring.context;
    requires static lombok;
    requires spring.security.oauth2.client;
}