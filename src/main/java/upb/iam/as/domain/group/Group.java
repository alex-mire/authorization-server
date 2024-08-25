package upb.iam.as.domain.group;

import jakarta.validation.constraints.NotBlank;
import org.springframework.data.annotation.*;
import org.springframework.data.relational.core.mapping.Table;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;

@Table(name = "group", schema = "authorization_service")
public record Group(
        @Id
        UUID id,
        @NotBlank
        String name,
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
}
