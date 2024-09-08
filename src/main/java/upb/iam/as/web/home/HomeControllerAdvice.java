package upb.iam.as.web.home;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import upb.iam.as.shared.BadRequestException;

@ControllerAdvice
public class HomeControllerAdvice {
    @ExceptionHandler(BadRequestException.class)
    public String handleBadRequestException(BadRequestException ex, RedirectAttributes redirectAttributes) {
        redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
        return "redirect:/home";
    }
}
