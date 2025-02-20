module site.gutschi.humble.spring.integration.ui {
    requires spring.context;
    requires lombok;
    //noinspection Java9RedundantRequiresStatement used for lombok
    requires org.slf4j;
    requires site.gutschi.humble.spring.tasks;
    requires spring.web;
    requires site.gutschi.humble.spring.common;
    requires site.gutschi.humble.spring.users;
    requires spring.security.config;
    requires spring.security.web;
    requires spring.security.core;
    requires thymeleaf.spring6;
    requires thymeleaf;
    requires unbescape;
    requires spring.beans;
    requires org.apache.tomcat.embed.core;
    requires jakarta.validation;
    requires spring.webmvc;
    requires spring.core;
    requires spring.boot.autoconfigure;
    requires spring.jcl;
}