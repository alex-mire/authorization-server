package upb.iam.as.web.authorization;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import upb.iam.as.shared.BadRequestException;

@ControllerAdvice
public class AuthorizationControllerAdvice {
    @ExceptionHandler(BadRequestException.class)
    public String handleBadRequestException(BadRequestException ex, RedirectAttributes redirectAttributes) {
        redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
        return "redirect:/authorizations";
    }
}
