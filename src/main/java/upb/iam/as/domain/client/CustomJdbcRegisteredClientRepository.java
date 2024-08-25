package upb.iam.as.domain.client;

import org.springframework.data.jdbc.repository.query.Modifying;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface CustomJdbcRegisteredClientRepository extends CrudRepository<Client, String>  {
    @Query("""
           select client_id
           from oauth2_registered_client
           """)
    List<String> findAllClientIds();

    @Modifying
    @Query("""
           delete from oauth2_registered_client
           where client_id = :clientId
           """)
    void deleteByClientId(String clientId);
}
