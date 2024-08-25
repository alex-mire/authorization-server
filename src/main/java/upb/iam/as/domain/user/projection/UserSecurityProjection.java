package upb.iam.as.domain.user.projection;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.beans.BeanUtils;
import upb.iam.as.domain.user.User;
import upb.iam.as.web.user.dto.AddUserDto;
import upb.iam.as.web.user.dto.UpdateUserDto;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.PROPERTY, property = "@class")
public class UserSecurityProjection implements Serializable {
    @Serial
    private static final long serialVersionUID = 1905122041950251207L;
    private String username;
    private String password;
    private boolean isAccountNonExpired;
    private boolean isAccountNonLocked;
    private boolean isCredentialsNonExpired;
    private boolean isEnabled;
    private List<String> authorities;

    public UserSecurityProjection(AddUserDto userDto, List<String> authorities) {
        BeanUtils.copyProperties(userDto, this);
        this.authorities = authorities;
    }

    public UserSecurityProjection(User user, UpdateUserDto userDto) {
        BeanUtils.copyProperties(user, this);
        BeanUtils.copyProperties(userDto, this);
    }
}
