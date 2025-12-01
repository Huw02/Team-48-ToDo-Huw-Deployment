package com.example.kromannreumert.user.service;

import com.example.kromannreumert.logging.entity.LogAction;
import com.example.kromannreumert.logging.entity.Logging;
import com.example.kromannreumert.logging.service.LoggingService;
import com.example.kromannreumert.user.dto.RoleRequestDTO;
import com.example.kromannreumert.user.dto.RoleResponseDTO;
import com.example.kromannreumert.user.entity.Role;
import com.example.kromannreumert.user.mapper.RoleMapper;
import com.example.kromannreumert.user.repository.RoleRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class RoleService {

    private final RoleRepository roleRepository;
    private final RoleMapper roleMapper;
    private final LoggingService loggingService;


    public RoleService(RoleRepository roleRepository, RoleMapper roleMapper, LoggingService loggingService) {
        this.roleRepository = roleRepository;
        this.roleMapper = roleMapper;
        this.loggingService = loggingService;
    }

    public List<RoleResponseDTO>getAllRoles(String name) {
        try {
            List<Role> roles = roleRepository.findAll();

            List<RoleResponseDTO> dtoList = roles.stream()
                    .map(roleMapper::toRoleResponseDTO)
                    .toList();

            loggingService.log(LogAction.VIEW_ALL_ROLES, name, "Viewed all roles");

            return dtoList;
        } catch (RuntimeException e) {

            loggingService.log(LogAction.VIEW_ALL_ROLES_FAILED, name, "Failed to view all roles");

            throw new RuntimeException("could not view the roles");
        }
    }

    //nedenstående er til frontend
    public RoleResponseDTO getRolebyRoleId(int roleId, String name) {
        try {
            Optional<Role> role = roleRepository.findById(roleId);

            List<RoleResponseDTO> dtoList = role.stream()
                    .map(roleMapper::toRoleResponseDTO)
                    .toList();

            loggingService.log(LogAction.VIEW_ONE_ROLE, name, "View one role");

            return dtoList.getFirst();
        } catch (RuntimeException e) {
            loggingService.log(LogAction.VIEW_ONE_ROLE_FAILED, name, "Failed to view one role");
            throw new RuntimeException("could not get role by role id");
        }
    }
    //nedenstående er til usermapper
    public Role getRoleById(int id){
        Optional<Role> role = roleRepository.findById(id);
        return role.get();
    }

    public RoleResponseDTO createRole(RoleRequestDTO roleRequestDTO, String name) {
        try {
            Role role = new Role(
                    roleRequestDTO.role()
            );

            Role roleFromDb = roleRepository.save(role);

            loggingService.log(LogAction.CREATE_ROLE, name, "Created a role: " + role.getRoleName());

            return new RoleResponseDTO(
                    roleFromDb.getId(),
                    roleFromDb.getRoleName()
            );
        } catch (RuntimeException e) {
            loggingService.log(LogAction.CREATE_ROLE_FAILED, name, "Failed to create role: " + roleRequestDTO.role());
            throw new RuntimeException("Could not create role");
        }
    }


    public RoleResponseDTO updateRole(int roleId, RoleRequestDTO requestDTO, String name) {
        try {
            Role role = roleRepository.findById(roleId).get();

            role.setRoleName(requestDTO.role());

            Role roleFromDb = roleRepository.save(role);

            loggingService.log(LogAction.UPDATE_ROLE, name, "Updated role with roleId: " + roleId + ", new role is: " + roleFromDb.getRoleName());

            return new RoleResponseDTO(
                    roleFromDb.getId(),
                    roleFromDb.getRoleName()
            );
        } catch (RuntimeException e) {
            loggingService.log(LogAction.UPDATE_ROLE_FAILED, name, "Failed to update role with roleId: " + roleId);
            throw new RuntimeException("could not update role");
        }
    }

    public void deleteRole(int roleId, String name) {
        try {
            roleRepository.deleteById(roleId);

            loggingService.log(LogAction.DELETE_ROLE, name, "Deleted role with role id: " + roleId);
        } catch (RuntimeException e) {
            loggingService.log(LogAction.DELETE_ROLE_FAILED, name, "Failed to delete role with role id: " + roleId);
        }
    }
}
