package com.example.kromannreumert.user.controller;

import com.example.kromannreumert.user.dto.RoleRequestDTO;
import com.example.kromannreumert.user.dto.RoleResponseDTO;
import com.example.kromannreumert.user.service.RoleService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/v1/role")
public class RoleController {


    private final RoleService roleService;

    public RoleController(RoleService roleService) {
        this.roleService = roleService;
    }


    @GetMapping("")
    public ResponseEntity<List<RoleResponseDTO>>getAllRoles(Principal principal){
        return ResponseEntity.ok(roleService.getAllRoles(principal.getName()));
    }

    @GetMapping("/{roleId}")
    public ResponseEntity<RoleResponseDTO>getSpecifikRole(@PathVariable int roleId, Principal principal){
        return new ResponseEntity<>(roleService.getRolebyRoleId(roleId, principal.getName()),
                HttpStatus.OK);
    }


    @PostMapping("")
    public ResponseEntity<RoleResponseDTO>createRole(@RequestBody RoleRequestDTO requestDTO, Principal principal){
        if(requestDTO != null){
            return new ResponseEntity<>(roleService.createRole(requestDTO, principal.getName()), HttpStatus.CREATED);
        } else {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
    }

    @PutMapping("/{roleId}")
    public ResponseEntity<RoleResponseDTO>updateRole(@PathVariable int roleId, @RequestBody RoleRequestDTO requestDTO, Principal principal){
        if(roleId != 0 && requestDTO != null && roleService.getRolebyRoleId(roleId, principal.getName()) != null){
            return new ResponseEntity<>(roleService.updateRole(roleId, requestDTO, principal.getName()), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("/{roleId}")
    public ResponseEntity<String>deleteRole(@PathVariable int roleId, Principal principal){
        if(roleId != 0 && roleService.getRolebyRoleId(roleId, principal.getName()) != null){
            roleService.deleteRole(roleId, principal.getName());
            return ResponseEntity.ok("The role was deleted");
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

}
