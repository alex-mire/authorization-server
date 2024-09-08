package upb.iam.as.web.client.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;

import java.util.List;
import java.util.Set;

@Data
public class RegisteredClientDto {

    @NotEmpty(message = "Client ID is required")
    private String clientId;

    private String clientSecret;

    @NotEmpty(message = "Client Authentication Method is required")
    private ClientAuthenticationMethod clientAuthenticationMethod;

    @NotEmpty(message = "Authorization Grant Type is required")
    private List<AuthorizationGrantType> authorizationGrantType;

    private String redirectUri;

    @NotEmpty(message = "Scopes are required")
    private String scopes;
}
