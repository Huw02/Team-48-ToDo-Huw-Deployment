package com.example.kromannreumert.user.controller;

import com.example.kromannreumert.user.dto.RoleRequestDTO;
import com.example.kromannreumert.user.dto.RoleResponseDTO;
import com.example.kromannreumert.user.entity.Role;
import com.example.kromannreumert.user.service.RoleService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/role")
public class RoleController {


    private final RoleService roleService;

    public RoleController(RoleService roleService) {
        this.roleService = roleService;
    }


    @GetMapping("")
    public ResponseEntity<List<RoleResponseDTO>>getAllRoles(){
        return ResponseEntity.ok(roleService.getAllRoles());
    }

    @GetMapping("{roleId}")
    public ResponseEntity<RoleResponseDTO>getSpecifikRole(@PathVariable int roleId){
        return new ResponseEntity<>(roleService.getRolebyRoleId(roleId),
                HttpStatus.OK);
    }


    @PostMapping("")
    public ResponseEntity<RoleResponseDTO>createRole(@RequestBody RoleRequestDTO requestDTO){
        if(requestDTO != null){
            return new ResponseEntity<>(roleService.createRole(requestDTO), HttpStatus.CREATED);
        } else {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
    }

    @PutMapping("{roleId}")
    public ResponseEntity<RoleResponseDTO>updateRole(@PathVariable int roleId, @RequestBody RoleRequestDTO requestDTO){
        if(roleId != 0 && requestDTO != null && roleService.getRolebyRoleId(roleId) != null){
            return new ResponseEntity<>(roleService.updateRole(roleId, requestDTO), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("{roleId}")
    public ResponseEntity<String>deleteRole(@PathVariable int roleId){
        if(roleId != 0 && roleService.getRolebyRoleId(roleId) != null){
            roleService.deleteRole(roleId);
            return ResponseEntity.ok("The role was deleted");
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

}
