package site.gutschi.humble.spring.main;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "site.gutschi.humble.spring")
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

}
