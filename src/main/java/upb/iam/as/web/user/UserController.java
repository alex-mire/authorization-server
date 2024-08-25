package upb.iam.as.web.user;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import upb.iam.as.domain.group.GroupRepository;
import upb.iam.as.domain.grouprole.GroupRoleRepository;
import upb.iam.as.domain.role.RoleNotFoundException;
import upb.iam.as.domain.role.RoleRepository;
import upb.iam.as.domain.user.UserRepository;
import upb.iam.as.domain.user.UserSecurity;
import upb.iam.as.domain.user.projection.UserSecurityProjection;
import upb.iam.as.domain.usergroup.UserGroup;
import upb.iam.as.domain.usergroup.UserGroupRepository;
import upb.iam.as.domain.userrole.UserRole;
import upb.iam.as.domain.userrole.UserRoleRepository;
import upb.iam.as.web.group.dto.UserGroupDto;
import upb.iam.as.web.role.dto.UserRoleDto;
import upb.iam.as.web.user.dto.*;
import upb.iam.as.shared.BadRequestException;

import java.util.*;
import java.util.stream.Collectors;

@Controller
@Transactional
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {

    private final UserDetailsManager userDetailsManager;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final GroupRepository groupRepository;
    private final UserRoleRepository userRoleRepository;
    private final UserGroupRepository userGroupRepository;
    private final GroupRoleRepository groupRoleRepository;

    @ModelAttribute
    public void addCommonAttributes(Model model) {
        model.addAttribute("roles", roleRepository.findAllRoleMinimalDto());
        model.addAttribute("groups", groupRepository.findAllGroupMinimalDtos());
    }

    @GetMapping
    public String users(Model model) {
        List<UserDto> users = userRepository.findAllUserDtos();
        model.addAttribute("users", users);
        model.addAttribute("addUser", new AddUserDto());
        return "users";
    }

    @GetMapping("/{id}")
    public String getUserDetails(@PathVariable UUID id, Model model) throws BadRequestException {
        UserDetailsDto user = userRepository.findUserDetailsDtoById(id)
                .map(UserDetailsDto::new)
                .orElseThrow(() -> new BadRequestException("User not found"));
        List<UserGroupDto> groups = groupRepository.findAllByUserId(id);
        Set<UserRoleDto> roles = roleRepository.findUserRoleDtoByUserId(id);
        user.setGroups(groups);
        user.setRoles(roles);
        model.addAttribute("user", user);
        model.addAttribute("updateUser", new UpdateUserDto());
        model.addAttribute("addUserGroup", new AddUserGroupDto());
        model.addAttribute("addUserRole", new AddUserRoleDto());
        return "userdetails";
    }

    @PostMapping("/create")
    public String createUser(@Valid @ModelAttribute("addUser") AddUserDto userDto, BindingResult result) {
        if (result.hasErrors()) {
            return "users";
        }
        List<String> authorities = new ArrayList<>();
        if (userDto.getRoleIds().isEmpty() && !userDto.getGroupIds().isEmpty()) {
            authorities.addAll(roleRepository.findRoleCodesByGroupId(userDto.getGroupIds()));
        }
        if (!userDto.getRoleIds().isEmpty() && !userDto.getGroupIds().isEmpty()) {
            authorities.addAll(roleRepository.findRoleCodesByGroupIdAndRoleIds(userDto.getGroupIds(), userDto.getRoleIds()));
        }

        Set<GrantedAuthority> simpleGrantedAuthorities = authorities.stream().map(SimpleGrantedAuthority::new).collect(Collectors.toSet());

        UserSecurity userSecurity = new UserSecurity(userDto.getUsername(),
                userDto.getPassword(),
                userDto.isAccountNonExpired(),
                userDto.isAccountNonLocked(),
                userDto.isCredentialsNonExpired(),
                userDto.isEnabled(),
                simpleGrantedAuthorities
                );

        userDetailsManager.createUser(userSecurity);

        insertUserGroup(userDto);

        return "redirect:/users";
    }

    @PostMapping("/{id}/update")
    public String updateUser(@PathVariable UUID id,
                             @Valid @ModelAttribute("updateUser") UpdateUserDto userDto,
                             BindingResult result) {
        if (result.hasErrors()) {
            return "redirect:/users/" + id;
        }

        var user = userRepository.findById(id)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        UserSecurity userSecurity = new UserSecurity(user.username(),
                user.password(),
                userDto.isAccountNonExpired(),
                userDto.isAccountNonLocked(),
                userDto.isCredentialsNonExpired(),
                userDto.isEnabled());
        userDetailsManager.updateUser(userSecurity);
        return "redirect:/users/" + id;
    }

    @PostMapping("/{id}/delete")
    public String deleteUser(@PathVariable UUID id) {
        var user = userRepository.findById(id)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        userDetailsManager.deleteUser(user.username());
        return "redirect:/users";
    }

    @PostMapping("/{id}/role/add")
    public String addUserRole(@PathVariable UUID id,
                              @Valid @ModelAttribute AddUserRoleDto addUseRoleDto,
                              BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "redirect:/users/" + id;
        }
        if (!userRepository.existsById(id)) {
            throw new BadRequestException("User not found");
        }
        if (!roleRepository.existsById(addUseRoleDto.getRoleId())) {
            throw new BadRequestException("Role not found");
        }
        if (userRoleRepository.existsByUserIdAndRoleId(id, addUseRoleDto.getRoleId())) {
            throw new BadRequestException("Role already exists on this user");
        }
        userRoleRepository.save(new UserRole(UUID.randomUUID(), id, addUseRoleDto.getRoleId(), null, null, 0));
        return "redirect:/users/" + id;
    }

    @PostMapping("/{id}/role/{roleId}/delete")
    public String deleteUserRole(@PathVariable UUID id,
                                 @PathVariable UUID roleId) {
        if (!userRepository.existsById(id)) {
            throw new BadRequestException("User not found");
        }
        if (!roleRepository.existsById(roleId)) {
            throw new BadRequestException("Role not found");
        }

        if (!userRoleRepository.existsByUserIdAndRoleId(id, roleId)) {
            throw new BadRequestException("Role not found on this user");
        }

        if (groupRoleRepository.existsByUserIdAndRoleId(id, roleId)) {
            throw new BadRequestException("Role is part of a group, remove from the group");
        }
        userRoleRepository.deleteByUserIdAndRoleId(id, roleId);
        return "redirect:/users/" + id;
    }

    @PostMapping("/{id}/group/add")
    public String addUserGroup(@PathVariable UUID id,
                               @Valid @ModelAttribute AddUserGroupDto addUserGroupDto,
                               BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "redirect:/users/" + id;
        }
        if (!userRepository.existsById(id)) {
            throw new BadRequestException("User not found");
        }
        if (!groupRepository.existsById(addUserGroupDto.getGroupId())) {
            throw new BadRequestException("Group not found");
        }
        if (userGroupRepository.existsByUserIdAndGroupId(id, addUserGroupDto.getGroupId())) {
            throw new BadRequestException("Group already exists on this user");
        }
        userGroupRepository.save(new UserGroup(UUID.randomUUID(), id, addUserGroupDto.getGroupId(), null, null, 0));
        return "redirect:/users/" + id;
    }

    @PostMapping("/{id}/group/{groupId}/delete")
    public String deleteUserGroup(@PathVariable UUID id,
                                  @PathVariable UUID groupId,
                                  BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            return "redirect:/users/" + id;
        }

        if (!userRepository.existsById(id)) {
            throw new BadRequestException("User not found");
        }
        if (!groupRepository.existsById(groupId)) {
            throw new BadRequestException("Group not found");
        }

        if (!userGroupRepository.existsByUserIdAndGroupId(id, groupId)) {
            throw new BadRequestException("Group not found on this user");
        }
        userGroupRepository.deleteByUserIdAndGroupId(id, groupId);
        return "redirect:/users/" + id;
    }

    private void insertUserGroup(AddUserDto userDto) {
        var userId = userRepository.findByUsername(userDto.getUsername()).orElseThrow(() -> new UsernameNotFoundException("User not found")).id();
        if (!userDto.getGroupIds().isEmpty()) {
            List<UUID> existingGroupIds = groupRepository.findAllByGroupIds(userDto.getGroupIds());
            if (existingGroupIds.size() != userDto.getGroupIds().size()) {
                throw new RoleNotFoundException("Some groups doesn't exist");
            }
            userDto.getGroupIds().forEach(groupId -> userGroupRepository.save(new UserGroup(UUID.randomUUID(), userId, groupId, null, null, 0)));
        }
    }
}
