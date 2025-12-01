package com.example.kromannreumert.user.mapper;

import com.example.kromannreumert.user.dto.RoleRequestDTO;
import com.example.kromannreumert.user.dto.RoleResponseDTO;
import com.example.kromannreumert.user.entity.Role;
import org.springframework.stereotype.Component;

@Component
public class RoleMapper {

    public Role toRole(RoleRequestDTO roleRequestDTO){
        return new Role(
                roleRequestDTO.roleName()
        );
    }

    public RoleResponseDTO toRoleResponseDTO(Role role){
        return new RoleResponseDTO(
                role.getId(),
                role.getRoleName()
        );
    }

}
