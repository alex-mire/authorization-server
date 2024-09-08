package upb.iam.as.web.role;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import upb.iam.as.domain.role.exceptions.RoleBadRequestException;

@ControllerAdvice
public class RoleControllerAdvice {
    @ExceptionHandler(RoleBadRequestException.class)
    public String handleRoleBadRequestException(RoleBadRequestException ex, RedirectAttributes redirectAttributes) {
        redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
        return "redirect:/roles";
    }

}
