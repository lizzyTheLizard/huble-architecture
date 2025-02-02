package site.gutschi.humble.spring.tasks.model;


import java.time.Instant;

public record Comment(String user, Instant timestamp, String text) {
}
