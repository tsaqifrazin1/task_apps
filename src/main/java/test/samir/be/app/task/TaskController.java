package test.samir.be.app.task;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import test.samir.be.app.common.annotation.ValueOfEnum;
import test.samir.be.app.common.constant.PriorityEnum;
import test.samir.be.app.common.constant.TaskSortBy;
import test.samir.be.app.common.response.WebResponse;
import test.samir.be.app.task.model.CreateTaskDTO;
import test.samir.be.app.task.model.FilterTaskDTO;
import test.samir.be.app.task.model.TaskResponse;
import test.samir.be.app.user.User;

import java.util.Date;
import java.util.List;

@RestController
@SecurityRequirement(name = "bearerAuth")
public class TaskController {

    @Autowired
    TaskService taskService;

    @PostMapping(
            path = "/api/tasks",
            produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE
    )
    public WebResponse<TaskResponse> createTask(@RequestBody CreateTaskDTO dto, @AuthenticationPrincipal User user) {
        TaskResponse taskResponse = taskService.createTask(dto, user);

        return WebResponse.<TaskResponse>builder().data(taskResponse).build();
    }

    @GetMapping(
            path = "/api/tasks/{id}"
    )
    public WebResponse<TaskResponse> getTask(@PathVariable("id") String id, @AuthenticationPrincipal User user) {
        TaskResponse taskResponse = taskService.findTaskById(id, user);

        return WebResponse.<TaskResponse>builder().data(taskResponse).build();
    }

    @PatchMapping(
            path = "/api/tasks/{id}/complete"
    )
    public WebResponse<TaskResponse> updateTaskComplete(@PathVariable("id") String id, @AuthenticationPrincipal User user) {
        TaskResponse taskResponse = taskService.updateTaskCompleted(id, user);

        return WebResponse.<TaskResponse>builder().data(taskResponse).build();
    }

    @GetMapping(
            path = "/api/tasks"
    )
    public WebResponse<List<TaskResponse>> findListTask(
            @RequestParam(required = false) boolean completed,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'") Date dueBefore,
            @RequestParam(required = false) @ValueOfEnum(enumClass = PriorityEnum.class) PriorityEnum priority,
            @RequestParam(required = false) @ValueOfEnum(enumClass = PriorityEnum.class) TaskSortBy sortBy,
            @RequestParam(required = false) String tag
            , @AuthenticationPrincipal User user) {
        FilterTaskDTO dto = new FilterTaskDTO();
        dto.setCompleted(completed);
        dto.setDueBefore(dueBefore);
        dto.setPriority(priority);
        dto.setSortBy(sortBy);
        dto.setTag(tag);

        List<TaskResponse> taskResponses = taskService.findTasks(dto, user);

        return WebResponse.
                <List<TaskResponse>>
                builder()
                .data(taskResponses)
                .build();
    }
}
