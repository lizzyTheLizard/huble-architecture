package site.gutschi.humble.spring.tasks.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import site.gutschi.humble.spring.common.helper.TimeHelper;
import site.gutschi.humble.spring.users.model.Project;
import site.gutschi.humble.spring.users.model.User;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;

class TaskTest {
    private Instant now;
    private User currentUser;
    private Task task;

    @BeforeEach
    void setUp() {
        currentUser = User.builder().email("dev@example.com").name("Hans").build();
        final var owner = User.builder().email("owner@example.com").name("Owner").build();
        final var project = Project.createNew("KEY", "Name", owner);
        task = Task.createNew(project, 13, "Name", "Title", owner);
        now = Instant.now();
        TimeHelper.setNow(now);
    }

    @Test
    void addComment() {
        final var comment = "Test Comment";

        task.addComment(comment, currentUser);

        assertThat(task.getComments()).contains(new Comment(currentUser, now, comment));
        assertThat(task.getHistoryEntries()).contains(new TaskHistoryEntry(currentUser, now, TaskHistoryType.COMMENTED, null, null, comment));
    }

    @Test
    void setStatus() {
        final var newStatus = TaskStatus.PROGRESS;
        final var oldStatus = task.getStatus();

        task.setStatus(newStatus, currentUser);

        assertThat(task.getStatus()).isEqualTo(newStatus);
        assertThat(task.getHistoryEntries()).contains(new TaskHistoryEntry(currentUser, now, TaskHistoryType.EDITED, "Status", oldStatus.name(), newStatus.name()));
    }

    @Test
    void setStatus_ignoreUnchanged() {
        final var historySize = task.getHistoryEntries().size();

        task.setStatus(task.getStatus(), currentUser);

        assertThat(task.getHistoryEntries()).hasSize(historySize);
    }

    @Test
    void setTitle() {
        final var oldTitle = task.getTitle();
        final var newTitle = "New Title";

        task.setTitle(newTitle, currentUser);

        assertThat(task.getTitle()).isEqualTo(newTitle);
        assertThat(task.getHistoryEntries()).contains(new TaskHistoryEntry(currentUser, now, TaskHistoryType.EDITED, "Title", oldTitle, newTitle));
    }

    @Test
    void setTitle_ignoreUnchanged() {
        final var historySize = task.getHistoryEntries().size();

        task.setTitle(task.getTitle(), currentUser);

        assertThat(task.getHistoryEntries()).hasSize(historySize);
    }

    @Test
    void setDescription() {
        final var oldDescription = task.getDescription();
        final var newDescription = "New Description";

        task.setDescription(newDescription, currentUser);

        assertThat(task.getDescription()).isEqualTo(newDescription);
        assertThat(task.getHistoryEntries()).contains(new TaskHistoryEntry(currentUser, now, TaskHistoryType.EDITED, "Description", oldDescription, newDescription));
    }

    @Test
    void setDescription_ignoreUnchanged() {
        final var historySize = task.getHistoryEntries().size();

        task.setDescription(task.getDescription(), currentUser);

        assertThat(task.getHistoryEntries()).hasSize(historySize);
    }

    @Test
    void setDeleted() {
        task.setDeleted(currentUser);

        assertThat(task.isDeleted()).isTrue();
        assertThat(task.getHistoryEntries()).contains(new TaskHistoryEntry(currentUser, now, TaskHistoryType.DELETED, null, null, null));
    }

    @Test
    void setEstimation() {
        final var newEstimation = 5;

        task.setEstimation(newEstimation, currentUser);

        assertThat(task.getEstimation()).contains(newEstimation);
        assertThat(task.getHistoryEntries()).contains(new TaskHistoryEntry(currentUser, now, TaskHistoryType.EDITED, "Estimation", null, "5"));
    }

    @Test
    void setEstimation_ignoreUnchanged() {
        final var historySize = task.getHistoryEntries().size();

        task.setEstimation(null, currentUser);

        assertThat(task.getHistoryEntries()).hasSize(historySize);
    }

    @Test
    void setEstimation_un_estimate() {
        final var oldEstimation = 5;
        task.setEstimation(oldEstimation, currentUser);

        task.setEstimation(null, currentUser);

        assertThat(task.getEstimation()).isEmpty();
        assertThat(task.getHistoryEntries()).contains(new TaskHistoryEntry(currentUser, now, TaskHistoryType.EDITED, "Estimation", "5", null));
    }

    @Test
    void setAssignee() {
        final var newAssignee = User.builder().email("asignee@example.com").name("Assignee").build();

        task.setAssignee(newAssignee, currentUser);

        assertThat(task.getAssignee()).contains(newAssignee);
        assertThat(task.getHistoryEntries()).contains(new TaskHistoryEntry(currentUser, now, TaskHistoryType.EDITED, "Assignee", null, newAssignee.getEmail()));
    }

    @Test
    void setAssignee_un_assign() {
        final var newAssignee = User.builder().email("asignee@example.com").name("Assignee").build();
        task.setAssignee(newAssignee, currentUser);

        task.setAssignee(null, currentUser);

        assertThat(task.getAssignee()).isEmpty();
        assertThat(task.getHistoryEntries()).contains(new TaskHistoryEntry(currentUser, now, TaskHistoryType.EDITED, "Assignee", newAssignee.getEmail(), null));

    }

    @Test
    void setAssignee_ignoreUnchanged() {
        final var historySize = task.getHistoryEntries().size();

        task.setAssignee(null, currentUser);

        assertThat(task.getHistoryEntries()).hasSize(historySize);
    }
}