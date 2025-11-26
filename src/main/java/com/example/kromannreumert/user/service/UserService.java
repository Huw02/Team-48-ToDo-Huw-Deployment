package com.example.kromannreumert.user.service;

import com.example.kromannreumert.logging.entity.LogAction;
import com.example.kromannreumert.logging.service.LoggingService;
import com.example.kromannreumert.user.entity.User;
import com.example.kromannreumert.user.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final LoggingService loggingService;
    private final static Logger log = LoggerFactory.getLogger(UserService.class);

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, LoggingService loggingService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.loggingService = loggingService;
    }

    @Override
    // ---- SPRING SECURITY "AUTO GENERATED" METHOD -----
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        // Get the user by username
        User user = userRepository.findByUsername(username).orElseThrow(() -> new UsernameNotFoundException("User not found"));

        // Get the users roles and convert it to "ROLE_xxx" and convert to a SimpleGrantedAuthority list
        List<SimpleGrantedAuthority> roleAuthority = user.getRoles().stream()
                .map(role -> new SimpleGrantedAuthority("ROLE_" + role.getRoleName()))
                .toList();

        // Return the new created object
        return org.springframework.security.core.userdetails.User
                .withUsername(user.getUsername())
                .password(user.getPassword())
                .authorities(roleAuthority)
                .build();
    }

    public String createUser(User user) {
        try {

            log.info("Trying to create user {}", user.getName());
            user.setPassword(passwordEncoder.encode(user.getPassword()));
            log.info("Successfully encrypted password {}", user.getName());
            userRepository.save(user);
            log.info("Successfully created {}", user.getName());
            loggingService.log(LogAction.CREATE_USER,"Zahaa","Created new user: " + user.getName());

            return "User created: " + user.getName();

        } catch (RuntimeException e) {

            log.error("Could not create user");
            loggingService.log(LogAction.CREATE_USER_FAILED,"TODO ADD AUTH USER","Created new user failed: " + user.getName());
            throw new RuntimeException("Could not create user");

        }
    }

    public User findUserByUsername(String username) {
        log.info("Service: Trying to find user by username {}", username);
        return userRepository.findByUsername(username).orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }
}
