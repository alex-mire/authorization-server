package upb.iam.as.domain.client;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;

@Table("oauth2_registered_client")
public record Client(
        @Id String id,
        String clientId,
        LocalDateTime clientIdIssuedAt,
        String clientSecret,
        LocalDateTime clientSecretExpiresAt,
        String clientName,
        String clientAuthenticationMethods,
        String authorizationGrantTypes,
        String redirectUris,
        String postLogoutRedirectUris,
        String scopes,
        String clientSettings,
        String tokenSettings
) {}
