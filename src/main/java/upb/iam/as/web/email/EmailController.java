package upb.iam.as.web.email;

import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import upb.iam.as.services.email.EmailValidationService;

@Controller
@RequestMapping("/email")
@RequiredArgsConstructor
@Transactional
public class EmailController {

    private final EmailValidationService emailValidationService;

    @GetMapping("/validation/{id}")
    public String validation(@PathVariable String id) {
        emailValidationService.validateEmailVerificationCode(id);
        return "emailvalidation";
    }
}
