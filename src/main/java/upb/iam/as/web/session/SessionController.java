package upb.iam.as.web.session;


import lombok.RequiredArgsConstructor;
import org.springframework.security.core.session.SessionInformation;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.session.SessionRegistryImpl;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/sessions")
@Transactional
@RequiredArgsConstructor
public class SessionController {

    private final SessionRegistry sessionRegistry;

    @GetMapping
    public String getAuthorizations(Model model) {
        List<SessionInformation> sessions = sessionRegistry.getAllPrincipals()
                .stream()
                .flatMap(principal -> sessionRegistry.getAllSessions(principal, true).stream())
                .toList();
        model.addAttribute("sessions", sessions);
        return "sessions";
    }
}
