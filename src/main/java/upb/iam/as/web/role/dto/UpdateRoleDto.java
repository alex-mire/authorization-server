package upb.iam.as.web.role.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class UpdateRoleDto {
    @NotBlank
    private String description;
}
