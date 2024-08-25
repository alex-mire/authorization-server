package upb.iam.as.domain.user;

import org.springframework.data.annotation.*;
import org.springframework.data.relational.core.mapping.Table;
import upb.iam.as.web.user.dto.UpdateUserDto;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;


@Table(name = "user", schema = "authorization_service")
public record User(
        @Id
        UUID id,
        String username,
        String password,
        boolean isAccountNonExpired,
        boolean isAccountNonLocked,
        boolean isCredentialsNonExpired,
        boolean isEnabled,
        @CreatedDate
        LocalDateTime createdDate,
        @CreatedBy
        String createdBy,
        @LastModifiedDate
        LocalDateTime lastModifiedDate,
        @LastModifiedBy
        String lastModifiedBy,
        @Version
        int version
) implements Serializable {
    public static User of(UUID id,
                          String username,
                          String password,
                          boolean isAccountNonExpired,
                          boolean isAccountNonLocked,
                          boolean isCredentialsNonExpired,
                          boolean isEnabled) {
        return new User(id, username, password, isAccountNonExpired, isAccountNonLocked, isCredentialsNonExpired, isEnabled, null, null, null,null,0);
    }
}
