package upb.iam.as.domain.user;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.UserDetailsManager;
import upb.iam.as.domain.role.RoleNotFoundException;
import upb.iam.as.domain.role.RoleRepository;
import upb.iam.as.domain.user.exception.UserBadRequestException;
import upb.iam.as.domain.useremail.UserEmailRepository;
import upb.iam.as.domain.usergroup.UserGroupRepository;
import upb.iam.as.domain.userrole.UserRole;
import upb.iam.as.domain.userrole.UserRoleRepository;
import upb.iam.as.shared.BadRequestException;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class UserSecurityDetailsManager implements UserDetailsManager {

    private final UserRepository userRepository;
    private final UserRoleRepository userRoleRepository;
    private final RoleRepository roleRepository;
    private final UserGroupRepository userGroupRepository;
    private final UserEmailRepository userEmailRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void createUser(UserDetails user) {
        UserSecurity userSecurity = (UserSecurity) user;
        if (userRepository.existsByUsername(userSecurity.getUsername())) {
            throw new UserBadRequestException("User with " + user.getUsername() + " already exists");
        }
        UUID userId = UUID.randomUUID();
        userRepository.save(User.of(userId,
                user.getUsername(),
                passwordEncoder.encode(user.getPassword()),
                user.isAccountNonExpired(),
                user.isAccountNonLocked(),
                user.isCredentialsNonExpired(),
                user.isEnabled()
        ));

        if (!user.getAuthorities().isEmpty()) {
            insertUserAuthorities(userId, user.getAuthorities());
        }
    }

    @Override
    public void updateUser(UserDetails user) {
        UserSecurity userSecurity = (UserSecurity) user;

        var userId = userRepository.findByUsername(userSecurity.getUsername())
                .orElseThrow(() -> new BadRequestException("User not found"))
                .id();

        userRepository.updateUser(userId,
                userSecurity.isAccountNonExpired(),
                userSecurity.isCredentialsNonExpired(),
                userSecurity.isAccountNonLocked(),
                userSecurity.isEnabled());
    }

    @Override
    public void deleteUser(String username) {
        var user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException(username));
        userRoleRepository.deleteByUserId(user.id());
        userGroupRepository.deleteByUserId(user.id());
        userEmailRepository.deleteByUserId(user.id());
        userRepository.deleteByUsername(username);
    }

    @Override
    public void changePassword(String oldPassword, String newPassword) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        var user = userRepository.findByUsername(authentication.getName()).orElseThrow(() -> new UsernameNotFoundException("User not found"));
        if (passwordEncoder.encode(oldPassword).equals(passwordEncoder.encode(newPassword))) {
            throw new RuntimeException("Passwords are the same");
        }
        userRepository.updateUserPassword(user.id(), passwordEncoder.encode(newPassword));

    }

    @Override
    public boolean userExists(String username) {
        return userRepository.existsByUsername(username);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        var user = userRepository.findDtoByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Username not found"));
        Set<GrantedAuthority> authorities = roleRepository.findRoleCodesByUsername(username).stream().map(SimpleGrantedAuthority::new).collect(Collectors.toSet());

        return new UserSecurity(user.getUsername(),
                user.getPassword(),
                user.isAccountNonExpired(),
                user.isAccountNonLocked(),
                user.isCredentialsNonExpired(),
                user.isEnabled(),
                authorities);
    }

    private void insertUserAuthorities(UUID userId, Collection<? extends GrantedAuthority> authorities) {
        List<String> roles = authorities.stream().map(GrantedAuthority::getAuthority).toList();
        List<UUID> roleIds = roleRepository.findAllByCodeIn(roles);
        if (roleIds.size() != authorities.size()) {
            throw new RoleNotFoundException("Some roles doesn't exist");
        }
        roleIds.forEach(roleId -> userRoleRepository.save(new UserRole(UUID.randomUUID(), userId, roleId, null, null, 0)));
    }
}
