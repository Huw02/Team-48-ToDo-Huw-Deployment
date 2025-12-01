package com.example.kromannreumert.user.controller;

import com.example.kromannreumert.user.dto.RoleResponseDTO;
import com.example.kromannreumert.user.dto.UserRequestDTO;
import com.example.kromannreumert.user.dto.UserResponseDTO;
import com.example.kromannreumert.user.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/v1/user")
public class UserController {


    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("")
    public ResponseEntity<List<UserResponseDTO>>getAllUsers(Principal principal){
        return ResponseEntity.ok(userService.getAllUsers(principal.getName()));
    }

    @GetMapping("/{userId}")
    public ResponseEntity<UserResponseDTO>getUserById(@PathVariable int userId, Principal principal){
        return new ResponseEntity<>(userService.getUserByUserId(userId, principal.getName()), HttpStatus.OK);
    }

    @PutMapping("/{userId}")
    public ResponseEntity<UserResponseDTO>updateUser(@PathVariable int userId, @RequestBody UserRequestDTO userRequestDTO, Principal principal){
        if(userId != 0 && userRequestDTO != null && userService.getUserByUserId(userId, principal.getName()) != null){
            return new ResponseEntity<>(userService.updateUser(userId, userRequestDTO, principal.getName()), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
    @DeleteMapping("/{userId}")
    public ResponseEntity<String>deleteUser(@PathVariable int userId, Principal principal){
        if(userId != 0 && userService.getUserByUserId(userId, principal.getName()) != null){
            userService.deleteUser(userId, principal.getName());
            return ResponseEntity.ok("Deleted user with user id: " + userId);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/size")
    public ResponseEntity<?> getUserNumber() {
        return new ResponseEntity<>(userService.getNumberOfAllUsers(), HttpStatus.OK);
    }

}
