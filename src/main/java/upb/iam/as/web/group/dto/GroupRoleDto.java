package upb.iam.as.web.group.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class GroupRoleDto {
    private UUID id;
    private String code;
    private String description;
    private String addedAt;
    private String addedBy;
}
