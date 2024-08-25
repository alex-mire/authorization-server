package upb.iam.as.domain.authorization;

import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.security.oauth2.server.authorization.OAuth2Authorization;

import java.util.List;

public interface AuthorizationRepository extends CrudRepository<Authorization, String> {

    @Query("""
           select id
           from oauth2_authorization
           """)
    List<String> findAllAuthorizationIds();
}
