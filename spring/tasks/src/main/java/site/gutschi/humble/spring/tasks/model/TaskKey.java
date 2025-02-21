package site.gutschi.humble.spring.tasks.model;

import site.gutschi.humble.spring.common.exception.InvalidInputException;

public record TaskKey(String projectKey, int taskId) {
    public static TaskKey fromString(String keyStr) {
        final var split = keyStr.split("-");
        if (split.length != 2) {
            final var message = String.format("'%s' is not a valid task key", keyStr);
            throw new InvalidInputException(message);
        }
        final var projectKey = split[0];
        try {
            final var id = Integer.parseInt(split[1]);
            return new TaskKey(projectKey, id);
        } catch (NumberFormatException e) {
            final var message = String.format("'%s' is not a valid task key", keyStr);
            throw new InvalidInputException(message);
        }
    }

    @Override
    public String toString() {
        return projectKey + "-" + taskId;
    }
}
