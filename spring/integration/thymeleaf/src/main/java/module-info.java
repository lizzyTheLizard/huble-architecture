module site.gutschi.humble.spring.integration.thymeleaf {
    //noinspection Java9RedundantRequiresStatement used for lombok
    requires org.slf4j;
    requires spring.web;
    requires site.gutschi.humble.spring.tasks;
    requires site.gutschi.humble.spring.users;
    requires org.apache.tomcat.embed.core;
    requires spring.context;
    requires thymeleaf.spring6;
    requires thymeleaf;
    requires spring.boot.autoconfigure;
    requires spring.webmvc;
    requires spring.beans;
    requires site.gutschi.humble.spring.common;
    requires jakarta.validation;
    requires spring.jcl;
    requires spring.core;
    requires unbescape;
    requires lombok;
}
