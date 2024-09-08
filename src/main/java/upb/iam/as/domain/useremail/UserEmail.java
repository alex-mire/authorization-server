package upb.iam.as.domain.useremail;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.annotation.*;
import org.springframework.data.relational.core.mapping.Table;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;

@Table(name = "user_email")
public record UserEmail(
        @Id
        UUID id,
        @NotNull
        UUID userId,
        @Email
        String email,
        @NotNull
        Boolean isDefault,
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
