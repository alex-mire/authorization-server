package upb.iam.as.domain.role;

import org.springframework.data.annotation.*;
import org.springframework.data.relational.core.mapping.Table;

import java.io.Serializable;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.UUID;

@Table(name = "role", schema = "authorization_service")
public record Role(
        @Id
        UUID id,
        String code,
        String description,
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
        public static Role of(UUID id, String code, String description) {
                return new Role(id, code, description, null, null, null, null, 0);
        }
}
