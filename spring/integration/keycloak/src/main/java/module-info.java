module site.gutschi.humble.spring.integration.thymeleaf {
    requires spring.context;
    //noinspection Java9RedundantRequiresStatement used for lombok
    requires org.slf4j;
    requires spring.security.core;
    requires site.gutschi.humble.spring.common;
    requires spring.security.config;
    requires spring.security.web;
    requires static lombok;
    requires spring.security.oauth2.core;
}