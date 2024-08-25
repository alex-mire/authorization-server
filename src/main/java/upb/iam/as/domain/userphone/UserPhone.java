package upb.iam.as.domain.userphone;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.annotation.*;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;
import java.util.UUID;

@Table(name = "USER_PHONE", schema = "AUTHORIZATION_SERVICE")
public record UserPhone(
        @Id
        UUID id,
        @NotNull
        UUID userId,
        @NotBlank
        String phoneNumber,
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
) {
}
