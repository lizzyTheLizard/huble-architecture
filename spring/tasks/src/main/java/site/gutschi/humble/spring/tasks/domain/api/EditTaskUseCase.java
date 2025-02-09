package site.gutschi.humble.spring.tasks.domain.api;

public interface EditTaskUseCase {
    void edit(EditTaskRequest request);

    void comment(CommentTaskRequest request);

    void delete(DeleteTaskRequest request);

}

