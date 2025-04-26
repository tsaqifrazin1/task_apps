package test.samir.be.app.tags.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import test.samir.be.app.common.constant.PriorityEnum;

import java.time.Instant;
import java.util.Date;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class TagResponse {
    private String id;
    private String name;
}
