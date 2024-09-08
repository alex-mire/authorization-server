package upb.iam.as.domain.user;

import org.springframework.data.jdbc.repository.query.Modifying;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import upb.iam.as.web.user.dto.AddUserDto;
import upb.iam.as.domain.user.projection.UserDetailsProjection;
import upb.iam.as.web.user.dto.UserDto;
import upb.iam.as.web.user.dto.UserMinimalDto;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends CrudRepository<User, UUID> {

    boolean existsByUsername(String username);

    @Query("""
            SELECT u.username,
                   u.password,
                   u.is_account_non_expired,
                   u.is_account_non_locked,
                   u.is_credentials_non_expired,
                   u.is_enabled
            FROM public.user u
            WHERE u.username = :username
            """)
    Optional<UserMinimalDto> findDtoByUsername(String username);

    @Query("""
            select case when exists(
                    select u.id
                    from public.user u
                    join user_role ur on ur.user_id = u.id
                    join role r on r.id = ur.role_id
                    where r.code = :code
                ) then 1 else 0 end;
            """)
    boolean existsByRole(String code);

    @Query("""
                       select
                       u.id,
                       u.username,
                       u.password,
                       ue.email,
                       u.is_account_non_expired,
                       u.is_account_non_locked,
                       u.is_credentials_non_expired,
                       u.is_enabled,
                       u.created_date,
                       u.created_by,
                       u.last_modified_date,
                       u.last_modified_by
                       from public.user u
                       left join public.user_email ue on ue.user_id = u.id
                       where u.id = :id
            """)
    Optional<UserDetailsProjection> findUserDetailsDtoById(UUID id);

    @Query("""
                       select
                       u.id,
                       u.username,
                       u.password,
                       u.is_account_non_expired,
                       u.is_credentials_non_expired,
                       u.is_account_non_locked,
                       u.is_enabled
                       from public.user u
            """)
    List<UserDto> findAllUserDtos();

    @Modifying
    @Query("""
             update public.user
             set is_account_non_expired     = :isAccountNonExpired,
                 is_credentials_non_expired = :isCredentialsNonExpired,
                 is_account_non_locked      = :isAccountNonLocked,
                 is_enabled                 = :isEnabled
             where id = :id
            """)
    void updateUser(@Param("id") UUID id,
                    @Param("isAccountNonExpired") boolean isAccountNonExpired,
                    @Param("isCredentialsNonExpired") boolean isCredentialsNonExpired,
                    @Param("isAccountNonLocked") boolean isAccountNonLocked,
                    @Param("isEnabled") boolean isEnabled);

    @Modifying
    @Query("""
             delete from public.user
             where username = :username
            """)
    void deleteByUsername(String username);

    Optional<User> findByUsername(String username);

    @Modifying
    @Query("""
             update public.user
             set password = :newPassword
             where id = :id
            """)
    void updateUserPassword(UUID id, String newPassword);
}
