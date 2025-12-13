package com.example.kromannreumert.user.mapper;

import com.example.kromannreumert.user.dto.UserRequestDTO;
import com.example.kromannreumert.user.dto.UserResponseDTO;
import com.example.kromannreumert.user.entity.Role;
import com.example.kromannreumert.user.entity.User;
import com.example.kromannreumert.user.service.RoleService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
public class UserMapper {

    private final RoleService roleService;
    private final PasswordEncoder passwordEncoder;

    public UserMapper(RoleService roleService, PasswordEncoder passwordEncoder) {
        this.roleService = roleService;
        this.passwordEncoder = passwordEncoder;
    }

    public User toUser(UserRequestDTO userRequestDTO){
        Role role = roleService.getRoleById(userRequestDTO.roleId());
        Set<Role>roles = new java.util.HashSet<>(Set.of(role));



        User user = new User();
        user.setUsername(userRequestDTO.username());
        user.setName(userRequestDTO.name());
        user.setEmail(userRequestDTO.email());
        user.setPassword(passwordEncoder.encode(userRequestDTO.password()));
        user.setRoles(roles);

        return user;

    }

    public UserResponseDTO toUserResponseDTO(User user){
        Role role = user.getRoles().stream().findFirst().get();

        return new UserResponseDTO(
                user.getUserId(),
                user.getUsername(),
                user.getName(),
                user.getEmail(),
                user.getCreatedDate(),
                role
                );
    }


}
