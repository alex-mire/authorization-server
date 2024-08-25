package upb.iam.as.web.group.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
public class UserGroupDto {
    private UUID id;
    private String name;
    private LocalDateTime addedAt;
    private String addedBy;
}
