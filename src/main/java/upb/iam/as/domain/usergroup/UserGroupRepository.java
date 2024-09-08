package upb.iam.as.domain.usergroup;

import jakarta.validation.constraints.NotNull;
import org.springframework.data.jdbc.repository.query.Modifying;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;
import java.util.UUID;

public interface UserGroupRepository extends CrudRepository<UserGroup, UUID> {
    Optional<UserGroup> findByUserIdAndGroupId(UUID userId, UUID groupId);
    boolean existsByUserIdAndGroupId(UUID id, UUID groupId);

    @Modifying
    @Query("""
           delete from user_group
           where user_id = :id and group_id = :groupId
           """)
    void deleteByUserIdAndGroupId(UUID id, UUID groupId);

    @Modifying
    @Query("""
           delete from user_group
           where user_id = :id
           """)
    void deleteByUserId(UUID id);
}
