package site.gutschi.humble.spring.tasks.model;


@SuppressWarnings("unused") //Used implicitly through UI
public enum TaskStatus {
    FUNNEL,
    READY,
    BACKLOG,
    TODO,
    PROGRESS,
    REVIEW,
    DONE,
    CANCELLED
}
