package upb.iam.as.domain.emailverification;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Version;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;
import java.util.UUID;

@Table("email_verification")
@Getter
@Setter
public class EmailVerification {
    @Id
    private UUID id;
    private String verificationCode;
    private String email;
    private String username;
    private boolean verified;
    @CreatedDate
    private LocalDateTime createdDate;
    @CreatedBy
    private String createdBy;
    @Version
    private int version;
}
