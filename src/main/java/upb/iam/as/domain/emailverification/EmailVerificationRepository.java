package upb.iam.as.domain.emailverification;

import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface EmailVerificationRepository extends CrudRepository<EmailVerification, String> {
    boolean existsByVerificationCode(String verificationCode);
    Optional<EmailVerification> findByVerificationCode(String verificationCode);
}
