package test.samir.be.app.user.model;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import test.samir.be.app.common.annotation.ValueOfEnum;
import test.samir.be.app.common.constant.PriorityEnum;

import java.util.Date;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CreateUserDTO {
    @NotBlank
    private String username;

    @NotBlank
    private String password;
}
