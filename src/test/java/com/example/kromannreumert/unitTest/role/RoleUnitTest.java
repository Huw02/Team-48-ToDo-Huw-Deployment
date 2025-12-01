package com.example.kromannreumert.unitTest.role;

import com.example.kromannreumert.logging.service.LoggingService;
import com.example.kromannreumert.user.dto.RoleResponseDTO;
import com.example.kromannreumert.user.entity.Role;
import com.example.kromannreumert.user.mapper.RoleMapper;
import com.example.kromannreumert.user.repository.RoleRepository;
import com.example.kromannreumert.user.service.RoleService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;


@ExtendWith(MockitoExtension.class)
public class RoleUnitTest {

    @InjectMocks
    RoleService roleService;

    @Mock
    RoleRepository roleRepository;

    @Mock
    RoleMapper roleMapper;

    @Mock
    LoggingService loggingService;



    @Test
    void getAllRoles(){

        // ARRANGE
        Role first = new Role(1L, "ADMIN");
        Role second = new Role(2L, "PARTNER");
        Role third = new Role(3L, "SAGSBEHANDLER");
        Role fourth = new Role(4L, "JURIST");
        List<Role>roles = Arrays.asList(first, second, third, fourth);

        RoleResponseDTO firstDto = new RoleResponseDTO(1L, "ADMIN");
        RoleResponseDTO secondDto = new RoleResponseDTO(2L, "PARTNER");
        RoleResponseDTO thirdDto = new RoleResponseDTO(3L, "SAGSBEHANDLER");
        RoleResponseDTO fourthDto = new RoleResponseDTO(4L, "JURIST");

        when(roleRepository.findAll()).thenReturn(roles);

        when(roleMapper.toRoleResponseDTO(first)).thenReturn(firstDto);
        when(roleMapper.toRoleResponseDTO(second)).thenReturn(secondDto);
        when(roleMapper.toRoleResponseDTO(third)).thenReturn(thirdDto);
        when(roleMapper.toRoleResponseDTO(fourth)).thenReturn(fourthDto);

        //ACT
        List<RoleResponseDTO>result = roleService.getAllRoles("testUser");

        //ASSERT
        assertNotNull(result);
        assertEquals(4, result.size());
        assertEquals("ADMIN", result.get(0).role());
        assertEquals(1L, result.get(0).id());

        //VERIFY
        verify(roleRepository, times(1)).findAll();
        verify(roleMapper, times(4)).toRoleResponseDTO(any(Role.class));

    }

    @Test
    void getRolebyRoleId(){
        //ARRANGE
        Role first = new Role(1L, "ADMIN");
        RoleResponseDTO firstDto = new RoleResponseDTO(1L, "ADMIN");
        Optional<Role>roleOptional = Optional.of(first);

        when(roleRepository.findById(1)).thenReturn(roleOptional);

        when(roleMapper.toRoleResponseDTO(first)).thenReturn(firstDto);



        //ACT
        RoleResponseDTO response = roleService.getRolebyRoleId(1, "testUser");


        //ASSERT
        assertNotNull(response);
        assertEquals("ADMIN", response.role());
        assertEquals(1L, response.id());


        //VERIFY
        verify(roleRepository, times(1)).findById(1);
        verify(roleMapper, times(1)).toRoleResponseDTO(any(Role.class));

    }

    @Test
    void getRoleById(){

    }


    @Test
    void createRole(){

    }

    @Test
    void updateRole(){

    }

    @Test
    void deleteRole(){

    }





}
