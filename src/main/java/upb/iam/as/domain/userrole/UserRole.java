package upb.iam.as.domain.userrole;

import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Version;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;
import java.util.UUID;

@Table(name = "user_role")
public record UserRole(
        @Id
        UUID id,
        UUID userId,
        UUID roleId,
        @CreatedDate
        LocalDateTime createdDate,
        @CreatedBy
        String createdBy,
        @Version
        int version
) {
}
