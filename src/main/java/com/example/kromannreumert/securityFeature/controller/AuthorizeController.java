package com.example.kromannreumert.securityFeature.controller;

import com.example.kromannreumert.securityFeature.JwtUtil.JwtGenerator;
import com.example.kromannreumert.securityFeature.dto.JwtResponseDTO;
import com.example.kromannreumert.securityFeature.dto.LoginDTO;
import com.example.kromannreumert.securityFeature.entity.User;
import com.example.kromannreumert.securityFeature.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthorizeController {

    private final AuthenticationManager authenticationManager;
    private final JwtGenerator jwtGenerator;
    private final UserService userService;

    public AuthorizeController(AuthenticationManager authenticationManager, JwtGenerator jwtGenerator, UserService userService) {
        this.authenticationManager = authenticationManager;
        this.jwtGenerator = jwtGenerator;
        this.userService = userService;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginDTO loginRequest) throws Exception {
        try {
            UsernamePasswordAuthenticationToken authenticationToken =
                    new UsernamePasswordAuthenticationToken(loginRequest.username(), loginRequest.password());

            Authentication user = authenticationManager.authenticate(authenticationToken);
            String token = jwtGenerator.issueToken(user.getName());

            return ResponseEntity.ok(new JwtResponseDTO(user.getName(), token, user.getAuthorities()));
        } catch (Exception e) {
            return new ResponseEntity<>("Could not sign user in", HttpStatus.BAD_REQUEST);
        }
    }

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
