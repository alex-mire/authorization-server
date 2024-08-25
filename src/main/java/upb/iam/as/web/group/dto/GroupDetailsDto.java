package upb.iam.as.web.group.dto;

import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.BeanUtils;
import org.springframework.context.annotation.Bean;
import upb.iam.as.domain.group.projection.GroupProjectionDto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
public class GroupDetailsDto {
    private UUID id;
    private String name;
    private LocalDateTime createdDate;
    private String createdBy;
    private LocalDateTime lastModifiedDate;
    private String lastModifiedBy;
    private List<GroupRoleDto> roles;

    public GroupDetailsDto(GroupProjectionDto group) {
        BeanUtils.copyProperties(group, this);
    }
}
