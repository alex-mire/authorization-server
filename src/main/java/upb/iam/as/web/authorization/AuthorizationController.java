package upb.iam.as.web.authorization;

import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.server.authorization.JdbcOAuth2AuthorizationService;
import org.springframework.security.oauth2.server.authorization.OAuth2Authorization;
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

    private final JdbcOAuth2AuthorizationService jdbcOAuth2AuthorizationService;
    private final AuthorizationRepository authorizationRepository;

    @GetMapping
    public String getAuthorizations(Model model) {
        var authorizations = authorizationRepository.findAllAuthorizationIds().stream()
                .map(jdbcOAuth2AuthorizationService::findById)
                .toList();
        model.addAttribute("authorizations", authorizations);
        return "authorizations";
    }
}
