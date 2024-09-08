package upb.iam.as.web.user;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.access.prepost.PreAuthorize;
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
import upb.iam.as.domain.user.exception.UserBadRequestException;
import upb.iam.as.domain.user.projection.UserSecurityProjection;
import upb.iam.as.domain.useremail.UserEmail;
import upb.iam.as.domain.useremail.UserEmailRepository;
import upb.iam.as.domain.usergroup.UserGroup;
import upb.iam.as.domain.usergroup.UserGroupRepository;
import upb.iam.as.domain.userrole.UserRole;
import upb.iam.as.domain.userrole.UserRoleRepository;
import upb.iam.as.events.UserCreatedEvent;
import upb.iam.as.services.accountholder.AccountHolderService;
import upb.iam.as.services.accountholder.dto.AssociateAccountHolderDto;
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
    private final UserEmailRepository userEmailRepository;
    private final ApplicationEventPublisher eventPublisher;
    private final AccountHolderService accountHolderService;

    @ModelAttribute
    public void addCommonAttributes(Model model) {
        model.addAttribute("roles", roleRepository.findAllRoleMinimalDto());
        model.addAttribute("groups", groupRepository.findAllGroupMinimalDtos());
    }

    @GetMapping
    @PreAuthorize("hasAnyAuthority('ADMIN', 'MANAGER')")
    public String users(Model model) {
        List<UserDto> users = userRepository.findAllUserDtos();
        model.addAttribute("users", users);
        model.addAttribute("addUser", new AddUserDto());
        model.addAttribute("accountHolder", accountHolderService.getAccountHolder());
        return "users";
    }

    @PostMapping("/{id}/associate")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'MANAGER')")
    public String associate(@PathVariable UUID id,
                            @Valid @ModelAttribute("associateAccountHolder") AssociateAccountHolderDto associateAccountHolderDto,
                            BindingResult result) {
        if (result.hasErrors()) {
            return "redirect:/users/" + id;
        }
        accountHolderService.associateAccountHolder(associateAccountHolderDto.getAccountHolderId(), id.toString());
        return "redirect:/users/" + id;
    }

    @PostMapping("/{id}/deassociate")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'MANAGER')")
    public String deassociate(@PathVariable UUID id,
                            @Valid @ModelAttribute("associateAccountHolder") AssociateAccountHolderDto associateAccountHolderDto,
                            BindingResult result) {
        if (result.hasErrors()) {
            return "redirect:/users/" + id;
        }
        accountHolderService.deassociateAccountHolder(associateAccountHolderDto.getAccountHolderId(), id.toString());
        return "redirect:/users/" + id;
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'MANAGER')")
    public String getUserDetails(@PathVariable UUID id, Model model) throws UserBadRequestException {
        UserDetailsDto user = userRepository.findUserDetailsDtoById(id)
                .map(UserDetailsDto::new)
                .orElseThrow(() -> new UserBadRequestException("User not found"));
        List<UserGroupDto> groups = groupRepository.findAllByUserId(id);
        Set<UserRoleDto> roles = roleRepository.findUserRoleDtoByUserId(id);
        user.setAccountHolderDto(accountHolderService.getaccountHolderByUserId(user.getId()));
        user.setGroups(groups);
        user.setRoles(roles);
        model.addAttribute("user", user);
        model.addAttribute("updateUser", new UpdateUserDto());
        model.addAttribute("addUserGroup", new AddUserGroupDto());
        model.addAttribute("addUserRole", new AddUserRoleDto());
        model.addAttribute("accountHolder", accountHolderService.getAccountHolder());
        model.addAttribute("associateAccountHolder", new AssociateAccountHolderDto());
        return "userdetails";
    }

    @PostMapping("/create")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'MANAGER')")
    public String createUser(@Valid @ModelAttribute("addUser") AddUserDto userDto, BindingResult result) {
        if (result.hasErrors()) {
            return "users";
        }

        Set<GrantedAuthority> simpleGrantedAuthorities = getAuthorities(userDto);

        var temporaryPassword = String.valueOf(new Random().nextInt(1000000000));
        UserSecurity userSecurity = new UserSecurity(userDto.getUsername(),
                temporaryPassword,
                userDto.isAccountNonExpired(),
                userDto.isAccountNonLocked(),
                userDto.isCredentialsNonExpired(),
                userDto.isEnabled(),
                simpleGrantedAuthorities
                );

        userDetailsManager.createUser(userSecurity);

        var userEmail = insertEmail(userDto.getUsername(), userDto.getEmail());
        insertUserGroup(userDto);
        eventPublisher.publishEvent(new UserCreatedEvent(userEmail, temporaryPassword));
        return "redirect:/users";
    }

    private UserEmail insertEmail(String username, String email) {
        var userId = userRepository.findByUsername(username).orElseThrow(() -> new UsernameNotFoundException("User not found")).id();
        var userEmail = new UserEmail(UUID.randomUUID(), userId, email, true, null, null, null, null, 0);
        return userEmailRepository.save(userEmail);
    }

    private Set<GrantedAuthority> getAuthorities(AddUserDto userDto) {
        List<String> authorities = new ArrayList<>();
        if (userDto.getRoleIds().isEmpty() && !userDto.getGroupIds().isEmpty()) {
            authorities.addAll(roleRepository.findRoleCodesByGroupId(userDto.getGroupIds()));
        }
        if (!userDto.getRoleIds().isEmpty() && !userDto.getGroupIds().isEmpty()) {
            authorities.addAll(roleRepository.findRoleCodesByGroupIdAndRoleIds(userDto.getGroupIds(), userDto.getRoleIds()));
        }

       return authorities.stream().map(SimpleGrantedAuthority::new).collect(Collectors.toSet());
    }

    @PostMapping("/{id}/update")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'MANAGER')")
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
    @PreAuthorize("hasAnyAuthority('ADMIN', 'MANAGER')")
    public String deleteUser(@PathVariable UUID id) {
        var user = userRepository.findById(id)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        userDetailsManager.deleteUser(user.username());
        return "redirect:/users";
    }

    @PostMapping("/{id}/role/add")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'MANAGER')")
    public String addUserRole(@PathVariable UUID id,
                              @Valid @ModelAttribute AddUserRoleDto addUseRoleDto,
                              BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "redirect:/users/" + id;
        }
        if (!userRepository.existsById(id)) {
            throw new UserBadRequestException("User not found");
        }
        if (!roleRepository.existsById(addUseRoleDto.getRoleId())) {
            throw new UserBadRequestException("Role not found");
        }
        if (userRoleRepository.existsByUserIdAndRoleId(id, addUseRoleDto.getRoleId())) {
            throw new UserBadRequestException("Role already exists on this user");
        }
        userRoleRepository.save(new UserRole(UUID.randomUUID(), id, addUseRoleDto.getRoleId(), null, null, 0));
        return "redirect:/users/" + id;
    }

    @PostMapping("/{id}/role/{roleId}/delete")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'MANAGER')")
    public String deleteUserRole(@PathVariable UUID id,
                                 @PathVariable UUID roleId) {
        if (!userRepository.existsById(id)) {
            throw new UserBadRequestException("User not found");
        }
        if (!roleRepository.existsById(roleId)) {
            throw new UserBadRequestException("Role not found");
        }

        if (!userRoleRepository.existsByUserIdAndRoleId(id, roleId)) {
            throw new UserBadRequestException("Role not found on this user");
        }

        if (groupRoleRepository.existsByUserIdAndRoleId(id, roleId)) {
            throw new UserBadRequestException("Role is part of a group, remove from the group");
        }
        userRoleRepository.deleteByUserIdAndRoleId(id, roleId);
        return "redirect:/users/" + id;
    }

    @PostMapping("/{id}/group/add")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'MANAGER')")
    public String addUserGroup(@PathVariable UUID id,
                               @Valid @ModelAttribute AddUserGroupDto addUserGroupDto,
                               BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "redirect:/users/" + id;
        }
        if (!userRepository.existsById(id)) {
            throw new UserBadRequestException("User not found");
        }
        if (!groupRepository.existsById(addUserGroupDto.getGroupId())) {
            throw new UserBadRequestException("Group not found");
        }
        if (userGroupRepository.existsByUserIdAndGroupId(id, addUserGroupDto.getGroupId())) {
            throw new UserBadRequestException("Group already exists on this user");
        }
        userGroupRepository.save(new UserGroup(UUID.randomUUID(), id, addUserGroupDto.getGroupId(), null, null, 0));
        return "redirect:/users/" + id;
    }

    @PostMapping("/{id}/group/{groupId}/delete")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'MANAGER')")
    public String deleteUserGroup(@PathVariable UUID id,
                                  @PathVariable UUID groupId,
                                  BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            return "redirect:/users/" + id;
        }

        if (!userRepository.existsById(id)) {
            throw new UserBadRequestException("User not found");
        }
        if (!groupRepository.existsById(groupId)) {
            throw new UserBadRequestException("Group not found");
        }

        if (!userGroupRepository.existsByUserIdAndGroupId(id, groupId)) {
            throw new UserBadRequestException("Group not found on this user");
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
