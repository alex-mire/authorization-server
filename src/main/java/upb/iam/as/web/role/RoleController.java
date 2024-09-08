package upb.iam.as.web.role;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import upb.iam.as.domain.role.Role;
import upb.iam.as.domain.role.RoleRepository;
import upb.iam.as.domain.role.exceptions.RoleBadRequestException;
import upb.iam.as.domain.role.exceptions.RoleDetailsBadRequestException;
import upb.iam.as.shared.BadRequestException;
import upb.iam.as.web.role.dto.AddRoleDto;
import upb.iam.as.web.role.dto.RoleDto;
import upb.iam.as.web.role.dto.UpdateRoleDto;
import upb.iam.as.web.user.dto.AddUserDto;

import java.util.List;
import java.util.UUID;

@Controller
@Transactional
@RequiredArgsConstructor
@RequestMapping("/roles")
public class RoleController {

    private final RoleRepository roleRepository;

    @GetMapping
    @PreAuthorize("hasAuthority('ADMIN')")
    public String users(Model model) {
        List<RoleDto> roles = roleRepository.findAllRoleMinimalDto();
        model.addAttribute("roles", roles);
        model.addAttribute("addRole", new AddRoleDto());
        model.addAttribute("updateRole", new UpdateRoleDto());
        return "roles";
    }
    @GetMapping("/error")
    @PreAuthorize("hasAuthority('ADMIN')")
    public String throwError() {
        throw new RoleBadRequestException("Test error");
    }

    @PostMapping("/add")
    @PreAuthorize("hasAuthority('ADMIN')")
    public String addRole(@Valid @ModelAttribute("addRole") AddRoleDto addRoleDto, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "redirect:/roles";
        }
        if (roleRepository.existsByCode(addRoleDto.getCode())) {
            throw new RoleBadRequestException("Role already exists");
        }
        roleRepository.save(Role.of(UUID.randomUUID(), addRoleDto.getCode(), addRoleDto.getDescription()));
        return "redirect:/roles";
    }

    @PostMapping("/{id}/update")
    @PreAuthorize("hasAuthority('ADMIN')")
    public String updateRole(@PathVariable UUID id,
                          @Valid @ModelAttribute("updateRole") UpdateRoleDto updateRoleDto,
                          BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "redirect:/roles/" + id;
        }
        if (!roleRepository.existsById(id)) {
            throw new RoleBadRequestException("Role not found");
        }
        roleRepository.updateRole(id, updateRoleDto.getDescription());
        return "redirect:/roles";
    }

    @PostMapping("/{id}/delete")
    @PreAuthorize("hasAuthority('ADMIN')")
    public String deleteRole(@PathVariable UUID id) {
        if (!roleRepository.existsById(id)) {
            throw new RoleBadRequestException("Role not found");
        }
        roleRepository.deleteRoleById(id);
        return "redirect:/roles";
    }

}
