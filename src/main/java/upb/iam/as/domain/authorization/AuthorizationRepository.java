package upb.iam.as.domain.authorization;

import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.security.oauth2.server.authorization.OAuth2Authorization;
import upb.iam.as.web.authorization.dto.AuthorizationDto;

import java.util.List;

public interface AuthorizationRepository extends CrudRepository<Authorization, String> {

    @Query("""
           select id,
                  registered_client_id,
                  principal_name,
                  authorization_grant_type,
                  authorized_scopes,
                  access_token_value,
                  access_token_issued_at,
                  access_token_expires_at,
                  refresh_token_value
           from oauth2_authorization
           """)
    List<AuthorizationDto> findAllAuthorizationIds();


}
