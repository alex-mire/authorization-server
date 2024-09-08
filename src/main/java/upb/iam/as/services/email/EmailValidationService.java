package upb.iam.as.services.email;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import upb.iam.as.domain.emailverification.EmailVerification;
import upb.iam.as.domain.emailverification.EmailVerificationRepository;
import upb.iam.as.domain.user.User;
import upb.iam.as.domain.user.UserRepository;
import upb.iam.as.domain.useremail.UserEmailRepository;
import upb.iam.as.shared.BadRequestException;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class EmailValidationService {
    private final UserRepository userRepository;
    private final EmailVerificationRepository emailVerificationRepository;
    private final UserEmailRepository userEmailRepository;

    public String generateEmailVerificationCode(String username, String email, String createdBy) {
        var user = userRepository.findByUsername(username)
                .orElseThrow(() -> new BadRequestException("User not found"));

        userEmailRepository.findByUserIdAndEmail(user.id(), email)
                .orElseThrow(() -> new BadRequestException("User email not found"));
        var emailVerification = new EmailVerification();
        emailVerification.setId(UUID.randomUUID());
        emailVerification.setUsername(username);
        emailVerification.setEmail(email);
        emailVerification.setVerified(false);
        emailVerification.setVerificationCode(UUID.randomUUID().toString());
        emailVerification.setCreatedBy(createdBy);
        emailVerification.setVersion(0);
        return emailVerificationRepository.save(emailVerification).getVerificationCode();
    }

    public boolean validateEmailVerificationCode(String code) {
         var emailValidation = emailVerificationRepository.findByVerificationCode(code)
                 .orElseThrow(() -> new BadRequestException("Validation not found"));

         var user = userRepository.findByUsername(emailValidation.getUsername())
                 .orElseThrow(() -> new BadRequestException("User not found"));

         userEmailRepository.findByUserIdAndEmail(user.id(), emailValidation.getEmail())
                 .orElseThrow(() -> new BadRequestException("User email not found"));

         userRepository.updateUser(user.id(), true, true, true, true);
         emailValidation.setVerified(true);
         emailVerificationRepository.save(emailValidation);
         return true;
    }
}
