package upb.iam.as.domain.grouprole;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.annotation.*;
import org.springframework.data.relational.core.mapping.Table;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;

@Table(name = "group_role", schema = "authorization_service")
public record GroupRole(
        @Id
        UUID id,
        @NotNull
        UUID groupId,
        @NotNull
        UUID roleId,
        @CreatedDate
        LocalDateTime createdDate,
        @CreatedBy
        String createdBy,
        @Version
        int version
) implements Serializable {
}
