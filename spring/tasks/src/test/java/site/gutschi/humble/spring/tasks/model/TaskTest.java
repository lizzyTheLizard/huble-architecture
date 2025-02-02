package site.gutschi.humble.spring.tasks.model;

import org.junit.jupiter.api.Test;
import site.gutschi.humble.spring.common.api.TimeApi;
import site.gutschi.humble.spring.common.api.UserApi;

import java.net.MalformedURLException;
import java.net.URL;
import java.time.Instant;
import java.time.ZonedDateTime;
import java.util.Map;

import static org.assertj.core.api.Assertions.*;

class TaskTest {
    private static final String USER = "TestUser";
    private static final UserApi USER_API = new UserApi() {
        @Override
        public String currentEmail() {
            return USER;
        }

        @Override
        public boolean isSystemAdmin() {
            return false;
        }
    };

    private static final Instant NOW = Instant.now();
    private static final TimeApi TIME_API = () -> NOW;

    @Test
    void addComment() {
        final var comment = "Test Comment";
        final var task = createTask();

        task.addComment(comment);

        assertThat(task.getComments())
                .contains(new Comment(USER, NOW, comment));
        assertThat(task.getHistoryEntries())
                .contains(new TaskHistoryEntry(USER, NOW, TaskHistoryType.COMMENTED, null, null, comment));
    }

    @Test
    void addImplementation() throws MalformedURLException {
        final var link = new URL("https://example.com");
        final var description = "Test Description";
        final var task = createTask();

        task.addImplementation(link, description);

        assertThat(task.getImplementations())
                .contains(new Implementation(link, description));
        assertThat(task.getHistoryEntries())
                .contains(new TaskHistoryEntry(USER, NOW, TaskHistoryType.IMPLEMENTED, null, null, link.toString()));
    }

    @Test
    void setStatus() {
        final var newStatus = TaskStatus.PROGRESS;
        final var task = createTask();
        final var oldStatus = task.getStatus();

        task.setStatus(newStatus);
        assertThat(task.getStatus())
                .isEqualTo(newStatus);
        assertThat(task.getHistoryEntries())
                .contains(new TaskHistoryEntry(USER, NOW, TaskHistoryType.STATUS_CHANGED, null, oldStatus.name(), newStatus.name()));
    }

    @Test
    void setStatus_ignoreUnchanged() {
        final var task = createTask();
        final var historySize = task.getHistoryEntries().size();

        task.setStatus(task.getStatus());
        assertThat(task.getHistoryEntries())
                .hasSize(historySize);
    }

    @Test
    void setTitle() {
        final var task = createTask();
        final var newTitle = "New Title";

        task.setTitle(newTitle);

        assertThat(task.getTitle())
                .isEqualTo(newTitle);
        assertThat(task.getHistoryEntries())
                .contains(new TaskHistoryEntry(USER, NOW, TaskHistoryType.TITLE_CHANGED, null, null, newTitle));
    }

    @Test
    void setTitle_ignoreUnchanged() {
        final var task = createTask();
        final var historySize = task.getHistoryEntries().size();

        task.setTitle(task.getTitle());

        assertThat(task.getHistoryEntries())
                .hasSize(historySize);
    }

    @Test
    void setDescription() {
        final var task = createTask();
        final var newDescription = "New Description";

        task.setDescription(newDescription);

        assertThat(task.getDescription())
                .isEqualTo(newDescription);
        assertThat(task.getHistoryEntries())
                .contains(new TaskHistoryEntry(USER, NOW, TaskHistoryType.DESCRIPTION_CHANGED, null, null, newDescription));
    }

    @Test
    void setDescription_ignoreUnchanged() {
        final var task = createTask();
        final var historySize = task.getHistoryEntries().size();

        task.setDescription(task.getDescription());

        assertThat(task.getHistoryEntries())
                .hasSize(historySize);
    }

    @Test
    void setAdditionalFields_initial() {
        final var field = "key2";
        final var newValue = "New Value";
        final var task = createTask();

        task.setAdditionalFields(Map.of(field, newValue));

        assertThat(task.getFields())
                .containsEntry(field, newValue);
        assertThat(task.getHistoryEntries())
                .contains(new TaskHistoryEntry(USER, NOW, TaskHistoryType.FIELD_CHANGED, field, null, newValue));
    }

    @Test
    void setField_update() {
        final var field = "key";
        final var newValue = "New Value";
        final var oldValue = "Old Value";
        final var task = createTask();
        task.setAdditionalFields(Map.of(field, oldValue));

        task.setAdditionalFields(Map.of(field, newValue));

        assertThat(task.getFields())
                .containsEntry(field, newValue);
        assertThat(task.getHistoryEntries())
                .contains(new TaskHistoryEntry(USER, NOW, TaskHistoryType.FIELD_CHANGED, field, oldValue, newValue));
    }

    @Test
    void setField_ignoreUnchanged() {
        final var field = "key";
        final var oldValue = "Old Value";
        final var task = createTask();
        task.setAdditionalFields(Map.of(field, oldValue));
        final var historySize = task.getHistoryEntries().size();

        task.setAdditionalFields(Map.of(field, oldValue));
        assertThat(task.getHistoryEntries())
                .hasSize(historySize);
    }

    @Test
    void setDeleted() {
        final var task = createTask();

        task.setDeleted();
        assertThat(task.isDeleted())
                .isTrue();
        assertThat(task.getHistoryEntries())
                .contains(new TaskHistoryEntry(USER, NOW, TaskHistoryType.DELETED, null, null, null));
    }

    @Test
    void setEstimation() {
        final var task = createTask();
        final var newEstimation = 5;

        task.setEstimation(newEstimation);
        assertThat(task.getEstimation())
                .contains(newEstimation);
        assertThat(task.getHistoryEntries())
                .contains(new TaskHistoryEntry(USER, NOW, TaskHistoryType.ESTIMATED, null, null, "5"));
    }

    @Test
    void setEstimation_ignoreUnchanged() {
        final var task = createTask();
        final var historySize = task.getHistoryEntries().size();

        task.setEstimation(null);
        assertThat(task.getHistoryEntries())
                .hasSize(historySize);
    }

    @Test
    void setEstimation_un_estimate() {
        final var task = createTask();
        final var oldEstimation = 5;
        task.setEstimation(oldEstimation);

        task.setEstimation(null);
        assertThat(task.getEstimation())
                .isEmpty();
        assertThat(task.getHistoryEntries())
                .contains(new TaskHistoryEntry(USER, NOW, TaskHistoryType.ESTIMATED, null, "5", null));
    }

    @Test
    void setAssignee() {
        final var task = createTask();
        final var newAssignee = "New Assignee";

        task.setAssigneeEmail(newAssignee);
        assertThat(task.getAssigneeEmail())
                .contains(newAssignee);
        assertThat(task.getHistoryEntries())
                .contains(new TaskHistoryEntry(USER, NOW, TaskHistoryType.ASSIGNED, null, "Old Assignee", newAssignee));
    }

    @Test
    void setAssignee_un_assign() {
        final var task = createTask();
        final var newAssignee = "New Assignee";
        task.setAssigneeEmail(newAssignee);

        task.setAssigneeEmail(null);
        assertThat(task.getAssigneeEmail())
                .isEmpty();
        assertThat(task.getHistoryEntries())
                .contains(new TaskHistoryEntry(USER, NOW, TaskHistoryType.ASSIGNED, null, newAssignee, null));

    }

    @Test
    void setAssignee_ignoreUnchanged() {
        final var task = createTask();
        final var historySize = task.getHistoryEntries().size();

        task.setAssigneeEmail("Old Assignee");
        assertThat(task.getHistoryEntries())
                .hasSize(historySize);
    }

    private static Task createTask() {
        final var task = Task.builder()
                .userApi(USER_API)
                .timeApi(TIME_API)
                .id(1)
                .status(TaskStatus.BACKLOG)
                .build();
        task.addComment("Old Comment");
        task.setAssigneeEmail("Old Assignee");
        task.setStatus(TaskStatus.BACKLOG);
        return task;
    }
}