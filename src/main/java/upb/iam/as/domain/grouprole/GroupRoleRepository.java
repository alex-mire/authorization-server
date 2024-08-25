package upb.iam.as.domain.grouprole;

import jakarta.validation.constraints.NotNull;
import org.springframework.data.jdbc.repository.query.Modifying;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.UUID;

public interface GroupRoleRepository extends CrudRepository<GroupRole, UUID> {
    boolean existsByGroupIdAndRoleId(UUID groupId, UUID roleId);

    @Modifying
    @Query("""
           delete from authorization_service.group_role
           where group_id = :id and role_id = :roleId
           """)
    void deleteByGroupIdAndRoleId(UUID id, UUID roleId);

    @Query("""
           select case when exists(select r.id
                 from authorization_service.user u
                 join authorization_service.user_group ug on ug.user_id = u.id
                 join authorization_service.group g on g.id = ug.group_id
                 join authorization_service.group_role gr on gr.group_id = g.id
                 join authorization_service.role r on r.id = gr.role_id
                 where u.id = :id and r.id = :roleId
            ) then 1 else 0 end;
           """)
    boolean existsByUserIdAndRoleId(UUID id, UUID roleId);
}
