package upb.iam.as.config;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jdbc.repository.config.EnableJdbcAuditing;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import upb.iam.as.domain.role.Role;
import upb.iam.as.domain.role.RoleRepository;
import upb.iam.as.domain.user.User;
import upb.iam.as.domain.user.UserRepository;
import upb.iam.as.domain.userrole.UserRole;
import upb.iam.as.domain.userrole.UserRoleRepository;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Configuration
@RequiredArgsConstructor
@EnableJdbcAuditing
public class ProjectConfig {

    @Value("${as.username}")
    private String username;
    @Value("${as.password}")
    private String password;

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final UserRoleRepository userRoleRepository;
    private final PasswordEncoder passwordEncoder;

    @Bean
    @Transactional
    public CommandLineRunner dataInitializer() {
        return args -> {
            if (username == null || password == null) {
                this.username = "admin";
                this.password = "admin";
            }
            UUID userId = UUID.randomUUID();
            UUID roleId = UUID.randomUUID();
            UUID userRoleId = UUID.randomUUID();
            if (!userRepository.existsByRole("ADMIN")) {
                userRepository.save(new User(userId, username, passwordEncoder.encode(password), true, true, true, true,
                        LocalDateTime.now(), "system", LocalDateTime.now(), "system", 0));
                roleRepository.save(new Role(roleId, "ADMIN", "ADMIN", LocalDateTime.now(), "system", LocalDateTime.now(), "system", 0));
                userRoleRepository.save(new UserRole(userRoleId, userId, roleId, LocalDateTime.now(), "system", 0));
            }
        };
    }

    @Bean
    AuditorAware<String> auditorAware() {
        return () -> Optional
                .ofNullable(SecurityContextHolder.getContext())
                .map(SecurityContext::getAuthentication)
                .filter(Authentication::isAuthenticated)
                .map(Authentication::getName);
    }
}