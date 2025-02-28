package site.gutschi.humble.spring.users.model;

import lombok.Builder;

import java.time.Instant;

//TODO Modelling: Use User instead of String
@Builder
public record ProjectHistoryEntry(String user, Instant timestamp, ProjectHistoryType type,
                                  String affectedUser, String oldValue, String newValue) {
}
