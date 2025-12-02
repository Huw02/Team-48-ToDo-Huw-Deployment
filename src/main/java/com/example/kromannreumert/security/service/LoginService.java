package com.example.kromannreumert.security.service;

import com.example.kromannreumert.exception.customException.UserUnauthorizedException;
import com.example.kromannreumert.logging.entity.LogAction;
import com.example.kromannreumert.logging.service.LoggingService;
import com.example.kromannreumert.security.JwtUtil.JwtGenerator;
import com.example.kromannreumert.security.dto.JwtResponseDTO;
import com.example.kromannreumert.security.dto.LoginDTO;
import com.example.kromannreumert.user.entity.User;
import com.example.kromannreumert.user.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LoginService {

    private final static Logger log = LoggerFactory.getLogger(LoggingService.class);
    private final AuthenticationManager authenticationManager;
    private final UserService userService;
    private final JwtGenerator jwtIssuer;
    private final LoggingService loggingService;

    public LoginService(AuthenticationManager authenticationManager, UserService userService, JwtGenerator jwtIssuer, LoggingService loggingService) {
        this.authenticationManager = authenticationManager;
        this.userService = userService;
        this.jwtIssuer = jwtIssuer;
        this.loggingService = loggingService;
    }

    public JwtResponseDTO login(LoginDTO loginRequest) throws Exception {
        try {

            log.info("Service: A user is trying to login {}", loginRequest.username());
            // This is for spring security to handle username and password with bcrypt
            // as it does not retrieve roles from the DB, we have to do it manually
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.username(), loginRequest.password()
                    )
            );

            // For us to retrieve the user roles, we have to retrieve the user object
            User user = userService.findUserByUsername(loginRequest.username());
            log.info("Service: Credentials verified {}", user.getName());

            // Get the roles, extract it to a String list so it can be forwarded with the request
            List<String> roles = user.getRoles().stream()
                    .map(role -> role.getRoleName().toUpperCase())
                    .toList();
            log.info("Service: Retrieved roles from the user {}", roles);

            // Generate the JWT token
            String token = jwtIssuer.issueToken(user.getUsername(), roles);
            log.info("Service: Generating the token for the user {}", user.getUsername());

            // Return the JWT token
            loggingService.log(LogAction.LOGIN_SUCCESS, user.getUsername(), "User logged in");
            return new JwtResponseDTO(user.getUsername(), token, roles);

        } catch (UserUnauthorizedException e) {

            log.error("User could not log in {}", loginRequest.username());
            loggingService.log(LogAction.LOGIN_FAILED, loginRequest.username(),"User failed to login");
            throw new UserUnauthorizedException();

        }
    }
}
