package com.example.kromannreumert.securityFeature.service;

import com.example.kromannreumert.securityFeature.entity.LogAction;
import com.example.kromannreumert.securityFeature.entity.User;
import com.example.kromannreumert.securityFeature.repository.UserRepository;
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
    private final LoggingService log;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, LoggingService log) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.log = log;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username).orElseThrow(() -> new UsernameNotFoundException("User not found"));

        List<SimpleGrantedAuthority> roleAuthority = user.getRoles().stream()
                .map(role -> new SimpleGrantedAuthority("ROLE_" + role.getRoleName()))
                .toList();

        return org.springframework.security.core.userdetails.User
                .withUsername(user.getUsername())
                .password(user.getPassword())
                .authorities(roleAuthority)
                .build();
    }

    public String createUser(User user) {
        try {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
            userRepository.save(user);
            log.log(
                    LogAction.CREATE_USER,
                    "TODO ADD AUTH USER",
                    "Created new user: " + user.getName()
                    );
            return "User created: " + user.getName();
        } catch (RuntimeException e) {
            throw new RuntimeException("Could not create user");
        }
    }

    public User findUserByUsername(String username) {
        return userRepository.findByUsername(username).orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }
}
