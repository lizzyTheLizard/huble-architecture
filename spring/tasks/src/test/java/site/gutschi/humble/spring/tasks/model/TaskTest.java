package site.gutschi.humble.spring.tasks.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import site.gutschi.humble.spring.common.api.CurrentUserApi;
import site.gutschi.humble.spring.common.helper.TimeHelper;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;

class TaskTest {
    private static final String USER = "TestUser";
    private static final CurrentUserApi USER_API = Mockito.mock(CurrentUserApi.class);
    private static final Instant NOW = Instant.now();

    private static Task createTask() {
        final var task = Task.builder()
                .currentUserApi(USER_API)
                .id(1)
                .status(TaskStatus.BACKLOG)
                .build();
        task.addComment("Old Comment");
        task.setAssigneeEmail("Old Assignee");
        task.setStatus(TaskStatus.BACKLOG);
        return task;
    }

    @BeforeEach
    void setUp() {
        Mockito.when(USER_API.currentEmail()).thenReturn(USER);
        Mockito.when(USER_API.isSystemAdmin()).thenReturn(false);
        TimeHelper.setNow(NOW);
    }

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
    void setStatus() {
        final var newStatus = TaskStatus.PROGRESS;
        final var task = createTask();
        final var oldStatus = task.getStatus();

        task.setStatus(newStatus);
        assertThat(task.getStatus())
                .isEqualTo(newStatus);
        assertThat(task.getHistoryEntries())
                .contains(new TaskHistoryEntry(USER, NOW, TaskHistoryType.EDITED, "Status", oldStatus.name(), newStatus.name()));
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
                .contains(new TaskHistoryEntry(USER, NOW, TaskHistoryType.EDITED, "Title", null, newTitle));
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
                .contains(new TaskHistoryEntry(USER, NOW, TaskHistoryType.EDITED, "Description", null, newDescription));
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
                .contains(new TaskHistoryEntry(USER, NOW, TaskHistoryType.EDITED, "Estimation", null, "5"));
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
                .contains(new TaskHistoryEntry(USER, NOW, TaskHistoryType.EDITED, "Estimation", "5", null));
    }

    @Test
    void setAssignee() {
        final var task = createTask();
        final var newAssignee = "New Assignee";

        task.setAssigneeEmail(newAssignee);
        assertThat(task.getAssigneeEmail())
                .contains(newAssignee);
        assertThat(task.getHistoryEntries())
                .contains(new TaskHistoryEntry(USER, NOW, TaskHistoryType.EDITED, "Assignee", "Old Assignee", newAssignee));
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
                .contains(new TaskHistoryEntry(USER, NOW, TaskHistoryType.EDITED, "Assignee", newAssignee, null));

    }

    @Test
    void setAssignee_ignoreUnchanged() {
        final var task = createTask();
        final var historySize = task.getHistoryEntries().size();

        task.setAssigneeEmail("Old Assignee");
        assertThat(task.getHistoryEntries())
                .hasSize(historySize);
    }
}