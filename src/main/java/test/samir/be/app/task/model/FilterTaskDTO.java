package test.samir.be.app.task.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import test.samir.be.app.common.annotation.ValueOfEnum;
import test.samir.be.app.common.constant.PriorityEnum;
import test.samir.be.app.common.constant.TaskSortBy;

import java.util.Date;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FilterTaskDTO {
    private Boolean completed;

    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
    private Date dueBefore;

    @ValueOfEnum(enumClass = PriorityEnum.class)
    private PriorityEnum priority;

    private String tag;

    @ValueOfEnum(enumClass = TaskSortBy.class)
    private TaskSortBy sortBy;

}
