package upb.iam.as.web.user.dto;

import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.BeanUtils;
import upb.iam.as.domain.user.projection.UserDetailsProjection;
import upb.iam.as.web.group.dto.UserGroupDto;
import upb.iam.as.web.role.dto.UserRoleDto;

import java.time.LocalDateTime;
import java.util.*;

@Getter
@Setter
public class UserDetailsDto {
    private UUID id;
    private String username;
    private String email;
    private String password;
    private UserAccountHolderDto accountHolderDto;
    private boolean isAccountNonExpired;
    private boolean isAccountNonLocked;
    private boolean isCredentialsNonExpired;
    private boolean isEnabled;
    private LocalDateTime createdDate;
    private String createdBy;
    private LocalDateTime lastModifiedDate;
    private String lastModifiedBy;
    private List<UserGroupDto> groups = new ArrayList<>();
    private Set<UserRoleDto> roles = new LinkedHashSet<>();

    public UserDetailsDto(UserDetailsProjection user) {
        BeanUtils.copyProperties(user, this);
    }
}
