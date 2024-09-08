package upb.iam.as.web.client;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.security.oauth2.server.authorization.JdbcOAuth2AuthorizationService;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationService;
import org.springframework.security.oauth2.server.authorization.client.JdbcRegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import upb.iam.as.domain.client.CustomJdbcRegisteredClientRepository;
import upb.iam.as.domain.client.exceptions.ClientBadRequestException;
import upb.iam.as.domain.role.exceptions.RoleBadRequestException;
import upb.iam.as.shared.BadRequestException;
import upb.iam.as.web.client.dto.RegisteredClientDto;

import java.util.List;
import java.util.UUID;

@Controller
@RequestMapping("/clients")
@Transactional
@RequiredArgsConstructor
public class ClientController {
    //http://localhost:8080/oauth2/authorize?response_type=code&client_id=client&scope=openid&redirect_uri=https://springone.io/authorized&code_challenge=QYPAZ5NU8yvtlQ9erXrUYR-T5AGCjCF47vN-KsaI2A8&code_challenge_method=S256
    private final JdbcRegisteredClientRepository jdbcRegisteredClientRepository;
    private final CustomJdbcRegisteredClientRepository customJdbcRegisteredClientRepository;
    private final PasswordEncoder passwordEncoder;

    @ModelAttribute
    @PreAuthorize("hasAuthority('ADMIN')")
    public void addCommonAttributes(Model model) {
        model.addAttribute("authMethods",
                List.of(ClientAuthenticationMethod.CLIENT_SECRET_BASIC,
                        ClientAuthenticationMethod.CLIENT_SECRET_POST,
                        ClientAuthenticationMethod.CLIENT_SECRET_JWT,
                        ClientAuthenticationMethod.NONE));
        model.addAttribute("grantType",
                List.of(AuthorizationGrantType.CLIENT_CREDENTIALS.getValue(),
                        AuthorizationGrantType.AUTHORIZATION_CODE.getValue(),
                        AuthorizationGrantType.REFRESH_TOKEN.getValue()));
    }

    @PostMapping("/add")
    @PreAuthorize("hasAuthority('ADMIN')")
    public String addClient(@Valid @ModelAttribute("registeredClient") RegisteredClientDto registeredClientDto,
                            BindingResult bindingResult,
                            RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            return "clients";
        }

        var registeredClient = RegisteredClient.withId(UUID.randomUUID().toString())
                .clientId(registeredClientDto.getClientId())
                .clientSecret(passwordEncoder.encode(registeredClientDto.getClientSecret()))
                .clientAuthenticationMethod(registeredClientDto.getClientAuthenticationMethod())
                .redirectUri(registeredClientDto.getRedirectUri())
                .scope(registeredClientDto.getScopes());

        registeredClientDto.getAuthorizationGrantType().forEach(registeredClient::authorizationGrantType);

        jdbcRegisteredClientRepository.save(registeredClient.build());
        redirectAttributes.addFlashAttribute("message", "Client successfully created.");
        return "redirect:/clients";
    }

    @PostMapping("/{clientId}/delete")
    @PreAuthorize("hasAuthority('ADMIN')")
    public String addClient(@PathVariable String clientId,
                            RedirectAttributes redirectAttributes) {
        if (jdbcRegisteredClientRepository.findByClientId(clientId) == null) {
            throw new ClientBadRequestException("Client not found");
        }
        customJdbcRegisteredClientRepository.deleteByClientId(clientId);
        redirectAttributes.addFlashAttribute("message", "Client successfully deleted.");
        return "redirect:/clients";
    }

    @GetMapping
    @PreAuthorize("hasAuthority('ADMIN')")
    public String listClients(Model model) {
        var clients = customJdbcRegisteredClientRepository.findAllClientIds()
                .stream()
                .map(jdbcRegisteredClientRepository::findByClientId)
                .toList();
        model.addAttribute("clients", clients);
        model.addAttribute("addClient", new RegisteredClientDto());
        return "clients";
    }

    @ExceptionHandler(BadRequestException.class)
    public String handleBadRequestException(BadRequestException ex, RedirectAttributes redirectAttributes) {
        redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
        return "redirect:/groups";
    }
}

