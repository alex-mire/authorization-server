package upb.iam.as.web.role.dto;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@EqualsAndHashCode
public class UserRoleDto {
    private UUID id;
    private String code;
    private String description;
    private LocalDateTime addedAt;
    private String addedBy;
}
