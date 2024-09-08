package upb.iam.as.web.session;


import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import upb.iam.as.domain.session.SpringSessionRepository;
import upb.iam.as.web.session.dto.SessionDto;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Controller
@RequestMapping("/sessions")
@Transactional
@RequiredArgsConstructor
public class SessionController {

    final SpringSessionRepository springSessionRepository;

    @GetMapping
    @PreAuthorize("hasAuthority('ADMIN')")
    public String getAuthorizations(Model model) {
        var sessions = springSessionRepository
                .findAllSessions()
                .stream()
                .map(SessionDto::new)
                .toList();
        model.addAttribute("sessions", sessions);
        return "sessions";
    }
}
