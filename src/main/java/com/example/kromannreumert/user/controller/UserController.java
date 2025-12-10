package com.example.kromannreumert.user.controller;

import com.example.kromannreumert.user.dto.RoleResponseDTO;
import com.example.kromannreumert.user.dto.UserRequestDTO;
import com.example.kromannreumert.user.dto.UserResponseDTO;
import com.example.kromannreumert.user.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/v1/user")
@CrossOrigin(origins = "*")
public class UserController {


    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("")
    public ResponseEntity<List<UserResponseDTO>>getAllUsers(Principal principal){
        return ResponseEntity.ok(userService.getAllUsers(principal.getName()));
    }

    @GetMapping("/jurists")
    public ResponseEntity<List<UserResponseDTO>>getAllJurists(Principal principal){
        return ResponseEntity.ok(userService.getAllJurists(principal));
    }



    @GetMapping("/{userId}")
    public ResponseEntity<UserResponseDTO>getUserById(@PathVariable int userId, Principal principal){
        return new ResponseEntity<>(userService.getUserByUserId(userId, principal.getName()), HttpStatus.OK);
    }
    @PostMapping("")
    public ResponseEntity<UserResponseDTO> createAccount(@RequestBody UserRequestDTO user, Principal principal) {
        if(user != null){
            return new ResponseEntity<>(userService.createUser(user, principal.getName()), HttpStatus.OK);
        }else {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
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


    @GetMapping("/singleUser/{username}")
    public ResponseEntity<UserResponseDTO>getUserByUsername(@PathVariable String username){
        return new ResponseEntity<>(userService.getUserByUsername(username), HttpStatus.OK);
    }

}
