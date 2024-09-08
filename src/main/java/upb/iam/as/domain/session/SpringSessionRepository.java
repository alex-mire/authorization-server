package upb.iam.as.domain.session;

import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface SpringSessionRepository extends CrudRepository<SpringSession, String> {

    @Query("""
           select * from spring_session
           """)
    List<SpringSession> findAllSessions();
}
