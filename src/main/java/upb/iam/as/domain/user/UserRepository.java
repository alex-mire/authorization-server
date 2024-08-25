package upb.iam.as.domain.user;

import org.springframework.data.jdbc.repository.query.Modifying;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import upb.iam.as.web.user.dto.AddUserDto;
import upb.iam.as.domain.user.projection.UserDetailsProjection;
import upb.iam.as.web.user.dto.UserDto;

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
            FROM authorization_service.user u
            WHERE u.username = :username
            """)
    Optional<AddUserDto> findDtoByUsername(String username);

    @Query("""
            select case when exists(
                    select u.id
                    from authorization_service.user u
                    join authorization_service.user_role ur on ur.user_id = u.id
                    join authorization_service.role r on r.id = ur.role_id
                    where r.code = :code
                ) then 1 else 0 end;
            """)
    boolean existsByRole(String code);

    @Query("""
                       select
                       u.id,
                       u.username,
                       u.password,
                       u.is_account_non_expired,
                       u.is_account_non_locked,
                       u.is_credentials_non_expired,
                       u.is_enabled,
                       u.created_date,
                       u.created_by,
                       u.last_modified_date,
                       u.last_modified_by
                       from authorization_service.user u
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
                       from authorization_service.user u
            """)
    List<UserDto> findAllUserDtos();

    @Modifying
    @Query("""
             update authorization_service.user
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
             delete from authorization_service.user
             where username = :username
            """)
    void deleteByUsername(String username);

    Optional<User> findByUsername(String username);

    @Modifying
    @Query("""
             update authorization_service.user
             set password = :newPassword
             where id = :id
            """)
    void updateUserPassword(UUID id, String newPassword);
}
