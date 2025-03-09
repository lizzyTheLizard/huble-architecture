package site.gutschi.humble.spring.tasks.model;


import site.gutschi.humble.spring.users.model.User;

import java.time.Instant;

public record Comment(User user, Instant timestamp, String text) {
}
