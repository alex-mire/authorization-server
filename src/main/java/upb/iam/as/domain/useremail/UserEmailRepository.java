package upb.iam.as.domain.useremail;

import org.springframework.data.jdbc.repository.query.Modifying;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;
import java.util.UUID;

public interface UserEmailRepository extends CrudRepository<UserEmail, UUID> {
    Optional<UserEmail> findByUserIdAndEmail(UUID userId, String email);

    @Modifying
    @Query("""
           delete from user_email
           where user_id = :id
           """)
    void deleteByUserId(UUID id);
}
