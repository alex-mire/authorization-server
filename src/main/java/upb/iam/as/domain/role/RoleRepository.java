package upb.iam.as.domain.role;

import jakarta.validation.constraints.NotBlank;
import org.springframework.data.jdbc.repository.query.Modifying;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import upb.iam.as.web.group.dto.GroupRoleDto;
import upb.iam.as.web.role.dto.RoleDto;
import upb.iam.as.web.role.dto.UserRoleDto;

import java.util.List;
import java.util.Set;
import java.util.UUID;

public interface RoleRepository extends CrudRepository<Role, UUID> {

    @Query("""
           select id
           from role
           where code in (:codes)
           """)
    List<UUID> findAllByCodeIn(List<String> codes);

    @Query("""
           select r.code
           from public.group g
           join group_role gr on gr.group_id = g.id
           join role r on r.id = gr.role_id
           where g.id in (:groupId)
           union
           select r.code
           from role r
           where r.id in (:roleIds)
           """)
    Set<String> findRoleCodesByGroupIdAndRoleIds(List<UUID> groupId, Set<UUID> roleIds);

    @Query("""
           select r.code
           from public.group g
           join group_role gr on gr.group_id = g.id
           join role r on r.id = gr.role_id
           where g.id in (:groupId)
           """)
    Set<String> findRoleCodesByGroupId(List<UUID> groupId);

    @Query("""
           select r.code
           from role r
           join user_role ur on ur.role_id = r.id
           join public.user u on u.id = ur.user_id
           where u.username = :username
           union
           select r.code
           from role r
           join group_role gr on gr.role_id = r.id
           join public.group g on g.id = gr.group_id
           join user_group ug on ug.group_id = g.id
           join public.user u on u.id = ug.user_id
           where u.username = :username
           """)
    List<String> findRoleCodesByUsername(String username);

    @Query("""
           select
           r.id,
           r.code,
           r.description,
           ur.created_date as added_at,
           ur.created_by as added_by
           from role r
           join user_role ur on ur.role_id = r.id
           join public.user u on u.id = ur.user_id
           where u.id = :id
           union
           select
           r.id,
           r.code,
           r.description,
           ug.created_date as added_at,
           ug.created_by as added_by
           from role r
           join group_role gr on gr.role_id = r.id
           join public.group g on g.id = gr.group_id
           join user_group ug on ug.group_id = g.id
           join public.user u on u.id = ug.user_id
           where u.id = :id
           """)
    Set<UserRoleDto> findUserRoleDtoByUserId(UUID id);

    @Query("""
           select r.id,
                  r.code,
                  r.description,
                  r.created_date,
                  r.created_by,
                  r.last_modified_date,
                  r.last_modified_by
           from role r
           """)
    List<RoleDto> findAllRoleMinimalDto();

    boolean existsByCode(String code);

    @Modifying
    @Query("""
             update role
             set description = :description
             where id = :id
            """)
    void updateRole(UUID id, String description);

    @Modifying
    @Query("""
           delete from role
           where id = :id
           """)
    void deleteRoleById(UUID id);

    @Query("""
           select r.id,
                  r.code,
                  r.description,
                  r.created_date as added_at,
                  r.created_by as added_by
           from public.group g
           join group_role gr on gr.group_id = g.id
           join role r on r.id = gr.role_id
           where g.id = :id
           """)
    List<GroupRoleDto> findRolesByGroupId(UUID id);
}
