package upb.iam.as.domain.userphone;

import org.springframework.data.repository.CrudRepository;

import java.util.UUID;

public interface UserPhoneRepository extends CrudRepository<UserPhone, UUID> {
}
