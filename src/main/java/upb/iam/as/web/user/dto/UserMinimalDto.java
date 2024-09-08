package upb.iam.as.web.user.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserMinimalDto {
    private String username;
    private String password;
    private boolean isAccountNonExpired;
    private boolean isAccountNonLocked;
    private boolean isCredentialsNonExpired;
    private boolean isEnabled;
}
