package upb.iam.as.domain.group;

import org.springframework.data.jdbc.repository.query.Modifying;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import upb.iam.as.domain.group.projection.GroupProjectionDto;
import upb.iam.as.web.group.dto.GroupDto;
import upb.iam.as.web.group.dto.GroupMinimalDto;
import upb.iam.as.web.group.dto.UserGroupDto;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface GroupRepository extends CrudRepository<Group, UUID> {

    @Query("""
           select
           g.id,
           g.name,
           ug.created_date as added_at,
           ug.created_by as added_by
           from public.group g
           join user_group ug on ug.group_id = g.id
           join public.user u on ug.user_id = u.id
           where u.id = :id
           """)
    List<UserGroupDto> findAllByUserId(UUID id);

    @Query("""
           select g.id,
                  g.name
           from public.group g
           """)
    List<GroupMinimalDto> findAllGroupMinimalDtos();

    @Query("""
           select g.id,
                  g.name,
                  g.created_date,
                  g.created_by,
                  g.last_modified_date,
                  g.last_modified_by
           from public.group g
           """)
    List<GroupDto> findAllGroupDtos();

    @Query("""
           select g.id,
                  g.name,
                  g.created_date,
                  g.created_by,
                  g.last_modified_date,
                  g.last_modified_by
           from public.group g
           where g.id = :id
           """)
    Optional<GroupProjectionDto> findGroupProjectionById(UUID id);

    @Query("""
           select g.id
           from public.group g
           where g.id in (:groupIds)
           """)
    List<UUID> findAllByGroupIds(List<UUID> groupIds);

    boolean existsByName(String name);

    @Modifying
    @Query("""
           update public.group
           set name = :name
           where id = :id
           """)
    void updateGroup(UUID id, String name);

    @Modifying
    @Query("""
           delete from public.group
           where id = :id
           """)
    void deleteGroupById(UUID id);
}
