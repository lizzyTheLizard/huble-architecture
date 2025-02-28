package site.gutschi.humble.spring.tasks.model;


import java.time.Instant;

//TODO Modelling: Use User instead of String
public record Comment(String user, Instant timestamp, String text) {
}
