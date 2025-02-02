package site.gutschi.humble.spring.common.api;


import java.time.Instant;

@FunctionalInterface
public interface TimeApi {

    Instant now();
}
