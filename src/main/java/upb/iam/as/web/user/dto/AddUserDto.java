package upb.iam.as.web.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.Set;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AddUserDto {
    @NotBlank(message = "Username is mandatory")
    private String username;
    @Email(message = "Email is mandatory")
    private String email;
    @NotNull
    private String accountHolderId;
    @NotNull
    private boolean isAccountNonExpired;
    @NotNull
    private boolean isAccountNonLocked;
    @NotNull
    private boolean isCredentialsNonExpired;
    @NotNull
    private boolean isEnabled;
    private List<UUID> groupIds;
    private Set<UUID> roleIds;
}
