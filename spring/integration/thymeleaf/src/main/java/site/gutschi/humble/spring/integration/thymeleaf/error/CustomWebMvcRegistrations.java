package site.gutschi.humble.spring.integration.thymeleaf.error;

import org.springframework.boot.autoconfigure.web.servlet.WebMvcRegistrations;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.mvc.method.annotation.ExceptionHandlerExceptionResolver;


@Configuration
public class CustomWebMvcRegistrations implements WebMvcRegistrations {

    @Override
    public ExceptionHandlerExceptionResolver getExceptionHandlerExceptionResolver() {
        return new LoggingExceptionHandlerExceptionResolver();
    }

}
