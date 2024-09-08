package upb.iam.as.web.authorization.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.time.LocalDateTime;

@Getter
@Setter
public class AuthorizationDto {
    private String id;
    private String registeredClientId;
    private String principalName;
    private String authorizationGrantType;
    private String authorizedScopes;
    private String accessTokenValue;
    private LocalDateTime accessTokenIssuedAt;
    private LocalDateTime accessTokenExpiresAt;
    private String refreshTokenValue;
}
