package com.example.kromannreumert.security.controller;

import com.example.kromannreumert.security.dto.JwtResponseDTO;
import com.example.kromannreumert.security.dto.LoginDTO;
import com.example.kromannreumert.user.entity.User;
import com.example.kromannreumert.security.service.LoginService;
import com.example.kromannreumert.user.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping("/auth")
@CrossOrigin(origins = "*")
// ADD @EnableMethodSecurity
public class AuthorizeController {

    private static final Logger log = LoggerFactory.getLogger(AuthorizeController.class);
    private final UserService userService;
    private final LoginService loginService;

    public AuthorizeController(UserService userService, LoginService loginService) {
        this.loginService = loginService;
        this.userService = userService;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginDTO loginRequest) {
        try {
            log.info("Controller: Login has been accesed with {}", loginRequest.username());
            JwtResponseDTO response = loginService.login(loginRequest);
            log.info("Controller: Login was successful {}", response.username());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Controller: It was not possible to sign the user in {}", loginRequest.username());
            return ResponseEntity.badRequest().body("Could not sign user in");
        }
    }

    /*
    ADD @PreAuthorize("hasRole('ADMIN')") when we are ready for it. It sets security on method level, so if someone access it with
    an unauthorized jwt token they will get denied
     */
    @PostMapping("/create")
    public ResponseEntity<?> createAccount(@RequestBody User user, Principal principal) {
        try {
            log.info("User created controller accessed by {}", user.getName());
            String test = userService.createUser(user, principal.getName());
            return ResponseEntity.ok(test);
        } catch (RuntimeException e) {
            log.error("Could not create user {}", user.getName());
            return new ResponseEntity<>("Could not create user",HttpStatus.BAD_REQUEST);
        }
    }
}
