package upb.iam.as.web.user;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import upb.iam.as.domain.user.exception.UserBadRequestException;

@ControllerAdvice
public class UserControllerAdvice {
    @ExceptionHandler(UserBadRequestException.class)
    public String handleBadRequestException(UserBadRequestException ex, RedirectAttributes redirectAttributes) {
        redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
        return "redirect:/users";
    }
}
