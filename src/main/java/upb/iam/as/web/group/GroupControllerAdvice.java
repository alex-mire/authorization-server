package upb.iam.as.web.group;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import upb.iam.as.domain.group.GroupBadRequestException;

@ControllerAdvice
public class GroupControllerAdvice {
    @ExceptionHandler(GroupBadRequestException.class)
    public String handleGroupException(GroupBadRequestException ex, RedirectAttributes redirectAttributes) {
        redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
        return "redirect:/groups";
    }
}
