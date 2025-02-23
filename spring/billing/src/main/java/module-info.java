module site.gutschi.humble.spring.billing {
    requires lombok;
    //noinspection Java9RedundantRequiresStatement used for lombok
    requires org.slf4j;
    requires spring.context;
    requires site.gutschi.humble.spring.common;
    requires site.gutschi.humble.spring.users;
    requires site.gutschi.humble.spring.tasks;
    requires spring.beans;
    requires org.apache.commons.validator;
    exports site.gutschi.humble.spring.billing.usecases;
    exports site.gutschi.humble.spring.billing.ports;
    exports site.gutschi.humble.spring.billing.model;
}