package upb.iam.as.web.role.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class AddRoleDto {
    @NotBlank
    private String code;
    @NotBlank
    private String description;
}
