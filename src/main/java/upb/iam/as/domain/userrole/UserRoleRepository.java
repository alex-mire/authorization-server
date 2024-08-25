package upb.iam.as.domain.userrole;

import jakarta.validation.constraints.NotNull;
import org.springframework.data.jdbc.repository.query.Modifying;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.UUID;

public interface UserRoleRepository extends CrudRepository<UserRole, UUID> {
    void deleteAllByUserId(UUID userId);

    boolean existsByUserIdAndRoleId(UUID id, UUID roleId);

    @Modifying
    @Query("""
           delete from authorization_service.user_role
           where user_id = :id and role_id = :roleId
           """)
    void deleteByUserIdAndRoleId(UUID id, UUID roleId);
}
