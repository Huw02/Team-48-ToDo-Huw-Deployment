package com.example.kromannreumert.user.service;

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


    public RoleService(RoleRepository roleRepository, RoleMapper roleMapper) {
        this.roleRepository = roleRepository;
        this.roleMapper = roleMapper;
    }

    public List<RoleResponseDTO>getAllRoles(){
        List<Role>roles = roleRepository.findAll();

        List<RoleResponseDTO>dtoList = roles.stream()
                .map(roleMapper::toRoleResponseDTO)
                .toList();

        return dtoList;
    }
    public RoleResponseDTO getRolebyRoleId(int roleId){
        Optional<Role>role = roleRepository.findById(roleId);

        List<RoleResponseDTO>dtoList = role.stream()
                .map(roleMapper :: toRoleResponseDTO)
                .toList();

        return dtoList.getFirst();
    }
    public RoleResponseDTO createRole(RoleRequestDTO roleRequestDTO){
        Role role = new Role(
                roleRequestDTO.role()
        );

        Role roleFromDb =  roleRepository.save(role);

        return new RoleResponseDTO(
                roleFromDb.getId(),
                roleFromDb.getRoleName()
        );
    }


    public RoleResponseDTO updateRole(int roleId, RoleRequestDTO requestDTO){
        Role role = roleRepository.findById(roleId).get();

        role.setRoleName(requestDTO.role());

        Role roleFromDb = roleRepository.save(role);

        return new RoleResponseDTO(
                roleFromDb.getId(),
                roleFromDb.getRoleName()
        );
    }


    public void deleteRole(int roleId){
        roleRepository.deleteById(roleId);
    }
}
