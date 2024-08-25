package upb.iam.as.web.home;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import upb.iam.as.web.home.dto.ChangePasswordDto;

import java.util.Objects;

@Controller
@RequiredArgsConstructor
public class HomeController {
    private final UserDetailsManager userSecurityDetailsManager;

    @GetMapping("/")
    public String redirectToHome() {
        return "redirect:/home";
    }

    @GetMapping("/home")
    public String home(Model model) {
        var a = SecurityContextHolder.getContext().getAuthentication();
        model.addAttribute("auth", a);
        model.addAttribute("pass", new ChangePasswordDto());
        return "home";
    }

    @PostMapping
    public String changePassword(@Valid @ModelAttribute("changePasswordDto") ChangePasswordDto changePasswordDto, BindingResult result) {
        if (result.hasErrors()) {
            return "home";
        }

        if(!Objects.equals(changePasswordDto.getNewPassword(), changePasswordDto.getConfirmPassword())) {
            throw new ChangePasswordBadRequestException("Passwords are not the same");
        }

        userSecurityDetailsManager.changePassword(changePasswordDto.getOldPassword(), changePasswordDto.getNewPassword());

        return "redirect:/home";
    }

    @ExceptionHandler(ChangePasswordBadRequestException.class)
    public String handleRoleBadRequestException(ChangePasswordBadRequestException ex, RedirectAttributes redirectAttributes) {
        redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
        return "redirect:/home";
    }
}

