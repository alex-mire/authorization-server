package upb.iam.as.web.error;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class ErrorController implements org.springframework.boot.web.servlet.error.ErrorController {
    @RequestMapping("/error")
    public String handleError(HttpServletRequest request, Model model) {
        Object status = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);
        Object errorMessage = request.getAttribute(RequestDispatcher.ERROR_MESSAGE);
        model.addAttribute("status", status != null ? status.toString() : "Unknown Error");
        model.addAttribute("error", errorMessage != null ? errorMessage.toString() : "Unknown Error");
        model.addAttribute("message", "Something went wrong.");
        return "error";
    }
}
