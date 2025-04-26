package test.samir.be.app.tags.model;

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
public class FilterTagDTO {
    private String name;
}
