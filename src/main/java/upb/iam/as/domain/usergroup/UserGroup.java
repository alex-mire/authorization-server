package upb.iam.as.domain.usergroup;

import jakarta.validation.constraints.NotNull;
import org.springframework.data.annotation.*;
import org.springframework.data.relational.core.mapping.Table;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;

@Table(name = "user_group", schema = "authorization_service")
public record UserGroup(
        @Id
        UUID id,
        @NotNull
        UUID userId,
        @NotNull
        UUID groupId,
        @CreatedDate
        LocalDateTime createdDate,
        @CreatedBy
        String createdBy,
        @Version
        int version
) implements Serializable {
}
