package upb.iam.as.web.authorization;

import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import upb.iam.as.domain.authorization.AuthorizationRepository;

@Controller
@RequestMapping("/authorizations")
@Transactional
@RequiredArgsConstructor
public class AuthorizationController {

    private final AuthorizationRepository authorizationRepository;

    @GetMapping
    @PreAuthorize("hasAuthority('ADMIN')")
    public String getAuthorizations(Model model) {
        var authorizations = authorizationRepository.findAllAuthorizationIds().stream()
                .toList();
        model.addAttribute("authorizations", authorizations);
        return "authorizations";
    }
}
