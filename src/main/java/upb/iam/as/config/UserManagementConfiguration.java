package upb.iam.as.config;

import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.stereotype.Component;
import upb.iam.as.domain.group.GroupRepository;
import upb.iam.as.domain.role.RoleRepository;
import upb.iam.as.domain.user.UserRepository;
import upb.iam.as.domain.user.UserSecurityDetailsManager;
import upb.iam.as.domain.usergroup.UserGroupRepository;
import upb.iam.as.domain.userrole.UserRoleRepository;

@Component
public class UserManagementConfiguration {

    @Bean
    public UserDetailsManager userDetailsManager(PasswordEncoder passwordEncoder, UserRepository userRepository, UserRoleRepository userRoleRepository, RoleRepository roleRepository, GroupRepository groupRepository, UserGroupRepository userGroupRepository) {
        return new UserSecurityDetailsManager(userRepository, userRoleRepository, roleRepository, userGroupRepository, passwordEncoder);
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

}
