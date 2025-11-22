package com.example.kromannreumert.securityFeature.controller;

import com.example.kromannreumert.securityFeature.dto.JwtResponseDTO;
import com.example.kromannreumert.securityFeature.dto.LoginDTO;
import com.example.kromannreumert.securityFeature.entity.User;
import com.example.kromannreumert.securityFeature.service.LoginService;
import com.example.kromannreumert.securityFeature.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/auth")
// ADD @EnableMethodSecurity
public class AuthorizeController {

    private static Logger log = LoggerFactory.getLogger(AuthorizeController.class);
    private final UserService userService;
    private final LoginService loginService;

    public AuthorizeController(UserService userService, LoginService loginService) {
        this.loginService = loginService;
        this.userService = userService;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginDTO loginRequest) {
        try {
            log.info("Login has been accesed with {}", loginRequest.username());
            JwtResponseDTO response = loginService.login(loginRequest);
            log.info("Login was successful {}", response.username());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("It was not possible to sign the user in {}", loginRequest.username());
            return ResponseEntity.badRequest().body("Could not sign user in");
        }
    }

    /*
    ADD @PreAuthorize("hasRole('ADMIN')") when we are ready for it. It sets security on method level, so if someone access it with
    an unauthorized jwt token they will get denied
     */
    @PostMapping("/create")
    public ResponseEntity<?> createAccount(@RequestBody User user) {
        try {
            userService.createUser(user);
            return ResponseEntity.ok("User created: " + user.getName());
        } catch (RuntimeException e) {
            return new ResponseEntity<>("Could not create user",HttpStatus.BAD_REQUEST);
        }

    }
}
