package test.samir.be.app.task.model;

import lombok.*;
import test.samir.be.app.common.constant.PriorityEnum;
import test.samir.be.app.tags.Tag;
import test.samir.be.app.tags.model.TagResponse;

import java.time.Instant;
import java.util.Date;
import java.util.Set;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class TaskResponse {
    private String id;
    private String title;
    private String notes;
    private Date dueDate;
    private PriorityEnum priority;
    private boolean completed;
    private Instant createdAt;
    private Instant completedAt;
    private Set<TagResponse> tags;
}
