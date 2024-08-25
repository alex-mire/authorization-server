package upb.iam.as.domain.authorization;

import org.springframework.data.relational.core.mapping.Table;

import java.sql.Timestamp;

@Table("oauth2_authorization")
public record Authorization(
        String id,
        String registeredClientId,
        String principalName,
        String authorizationGrantType,
        String authorizedScopes,
        String attributes,
        String state,
        String authorizationCodeValue,
        Timestamp authorizationCodeIssuedAt,
        Timestamp authorizationCodeExpiresAt,
        String authorizationCodeMetadata,
        String accessTokenValue,
        Timestamp accessTokenIssuedAt,
        Timestamp accessTokenExpiresAt,
        String accessTokenMetadata,
        String accessTokenType,
        String accessTokenScopes,
        String oidcIdTokenValue,
        Timestamp oidcIdTokenIssuedAt,
        Timestamp oidcIdTokenExpiresAt,
        String oidcIdTokenMetadata,
        String refreshTokenValue,
        Timestamp refreshTokenIssuedAt,
        Timestamp refreshTokenExpiresAt,
        String refreshTokenMetadata,
        String userCodeValue,
        Timestamp userCodeIssuedAt,
        Timestamp userCodeExpiresAt,
        String userCodeMetadata,
        String deviceCodeValue,
        Timestamp deviceCodeIssuedAt,
        Timestamp deviceCodeExpiresAt,
        String deviceCodeMetadata
) {
}
