package upb.iam.as.web.user.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class AddUserRoleDto {
    @NotNull
    private UUID roleId;
}
