package test.samir.be.app.task;

import test.samir.be.app.task.model.CreateTaskDTO;
import test.samir.be.app.task.model.FilterTaskDTO;
import test.samir.be.app.task.model.TaskResponse;
import test.samir.be.app.user.User;

import java.util.List;

public interface TaskService {
    TaskResponse createTask(CreateTaskDTO dto, User user);
    TaskResponse findTaskById(String id, User user);
    TaskResponse updateTaskCompleted(String id, User user);
    List<TaskResponse> findTasks(FilterTaskDTO dto, User user);
}
