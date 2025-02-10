package site.gutschi.humble.spring.tasks.api;

public interface EditTaskUseCase {
    void edit(EditTaskRequest request);

    void comment(CommentTaskRequest request);

    void delete(DeleteTaskRequest request);

}

