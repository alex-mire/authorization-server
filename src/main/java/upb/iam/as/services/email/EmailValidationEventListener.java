package upb.iam.as.services.email;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationListener;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import upb.iam.as.domain.user.UserRepository;
import upb.iam.as.domain.useremail.UserEmail;
import upb.iam.as.events.UserCreatedEvent;
import upb.iam.as.shared.BadRequestException;


@Component
@RequiredArgsConstructor
public class EmailValidationEventListener implements ApplicationListener<UserCreatedEvent> {
    @Value("${as.baseurl}")
    private String baseUrl;
    private final JavaMailSender mailSender;
    private final EmailValidationService emailValidationService;
    private final UserRepository userRepository;

    public void onApplicationEvent(UserCreatedEvent userCreatedEvent) {
        var user = userRepository.findById(userCreatedEvent.getUser()).orElseThrow(
                () -> new BadRequestException("User not found")
        );
        String username = user.username();
        String email = userCreatedEvent.getEmail();
        String verificationId = emailValidationService.generateEmailVerificationCode(username, email, userCreatedEvent.getCreatedBy());

        SimpleMailMessage message = new SimpleMailMessage();
        message.setSubject("UPB Email Verification");
        message.setText("Validate your email address at the next link: " + baseUrl + "/email/validation/" + verificationId
                        + "<br> Your temporary password is: " + userCreatedEvent.getPassword());
        message.setTo(email);
        mailSender.send(message);
    }

}
