package upb.iam.as.web.group;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import upb.iam.as.domain.group.Group;
import upb.iam.as.domain.group.GroupBadRequestException;
import upb.iam.as.domain.group.GroupRepository;
import upb.iam.as.domain.grouprole.GroupRole;
import upb.iam.as.domain.grouprole.GroupRoleRepository;
import upb.iam.as.domain.role.RoleRepository;
import upb.iam.as.web.group.dto.AddGroupDto;
import upb.iam.as.web.group.dto.AddGroupRoleDto;
import upb.iam.as.web.group.dto.GroupDetailsDto;
import upb.iam.as.web.group.dto.GroupDto;

import java.util.List;
import java.util.UUID;

@Controller
@Transactional
@RequiredArgsConstructor
@RequestMapping("/groups")
public class GroupController {
    private final GroupRepository groupRepository;
    private final GroupRoleRepository groupRoleRepository;
    private final RoleRepository roleRepository;

    @ModelAttribute
    @PreAuthorize("hasAuthority('ADMIN')")
    public void addCommonAttributes(Model model) {
        model.addAttribute("roles", roleRepository.findAllRoleMinimalDto());
    }

    @GetMapping
    @PreAuthorize("hasAuthority('ADMIN')")
    public String users(Model model) {
        List<GroupDto> groups = groupRepository.findAllGroupDtos();
        model.addAttribute("groups", groups);
        model.addAttribute("addGroup", new AddGroupDto());
        return "groups";
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public String getUserDetails(@PathVariable UUID id, Model model)  {
        var group = groupRepository.findGroupProjectionById(id)
                .orElseThrow(() -> new GroupBadRequestException("Group not found"));
        GroupDetailsDto groupDetailsDto = new GroupDetailsDto(group);
        var groupRoles = roleRepository.findRolesByGroupId(group.getId());
        groupDetailsDto.setRoles(groupRoles);

        model.addAttribute("group", groupDetailsDto);
        model.addAttribute("addGroup", new AddGroupDto());
        model.addAttribute("addGroupRole", new AddGroupRoleDto());
        return "groupdetails";
    }

    @PostMapping("/add")
    @PreAuthorize("hasAuthority('ADMIN')")
    public String addRole(@Valid @ModelAttribute("addGroup") AddGroupDto addGroupDto, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "groups";
        }
        if (groupRepository.existsByName(addGroupDto.getName())) {
            throw new GroupBadRequestException("Group already exists");
        }
        groupRepository.save(new Group(UUID.randomUUID(), addGroupDto.getName(), null, null, null, null, 0));
        return "redirect:/groups";
    }

    @PostMapping("/{id}/update")
    @PreAuthorize("hasAuthority('ADMIN')")
    public String updateGroup(@PathVariable UUID id,
                          @Valid @ModelAttribute AddGroupDto addGroupDto,
                          BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "redirect:/groups/" + id;
        }
        if (!groupRepository.existsById(id)) {
            throw new GroupBadRequestException("Group not found");
        }
        groupRepository.updateGroup(id, addGroupDto.getName());
        return "redirect:/groups/" + id;
    }

    @PostMapping("/{id}/delete")
    @PreAuthorize("hasAuthority('ADMIN')")
    public String addRole(@PathVariable UUID id) {
        if (!groupRepository.existsById(id)) {
            throw new GroupBadRequestException("Group not found");
        }
        groupRepository.deleteGroupById(id);
        return "redirect:/groups";
    }

    @PostMapping("/{id}/role/add")
    @PreAuthorize("hasAuthority('ADMIN')")
    public String updateGroup(@PathVariable UUID id,
                              @Valid @ModelAttribute AddGroupRoleDto addGroupRoleDto,
                              BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "redirect:/groups/" + id;
        }
        if (!groupRepository.existsById(id)) {
            throw new GroupBadRequestException("Group not found");
        }
        if(!roleRepository.existsById(addGroupRoleDto.getRoleId())) {
            throw new GroupBadRequestException("Role not found");
        }

        if(groupRoleRepository.existsByGroupIdAndRoleId(id, addGroupRoleDto.getRoleId())) {
            throw new GroupBadRequestException("Role already exists on this group");
        }
        groupRoleRepository.save(new GroupRole(UUID.randomUUID(), id, addGroupRoleDto.getRoleId(),null, null, 0));
        return "redirect:/groups/" + id;
    }

    @PostMapping("/{id}/role/{roleId}/delete")
    @PreAuthorize("hasAuthority('ADMIN')")
    public String updateGroup(@PathVariable UUID id,
                              @PathVariable UUID roleId) {
        if (!groupRepository.existsById(id)) {
            throw new GroupBadRequestException("Group not found");
        }
        if(!roleRepository.existsById(roleId)) {
            throw new GroupBadRequestException("Role not found");
        }

        if(!groupRoleRepository.existsByGroupIdAndRoleId(id, roleId)) {
            throw new GroupBadRequestException("Role not found on group");
        }
        groupRoleRepository.deleteByGroupIdAndRoleId(id, roleId);
        return "redirect:/groups/" + id;
    }
}
