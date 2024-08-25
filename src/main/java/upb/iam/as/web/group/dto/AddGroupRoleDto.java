package upb.iam.as.web.group.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class AddGroupRoleDto {
    @NotNull
    private UUID roleId;
}
