package upb.iam.as.domain.useremail;

import org.springframework.data.repository.CrudRepository;

import java.util.UUID;

public interface UserEmailRepository extends CrudRepository<UserEmail, UUID> {
}
