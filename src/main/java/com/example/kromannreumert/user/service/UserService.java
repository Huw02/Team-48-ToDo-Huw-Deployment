package com.example.kromannreumert.user.service;

import com.example.kromannreumert.logging.entity.LogAction;
import com.example.kromannreumert.logging.service.LoggingService;
import com.example.kromannreumert.user.dto.UserRequestDTO;
import com.example.kromannreumert.user.dto.UserResponseDTO;
import com.example.kromannreumert.user.entity.Role;
import com.example.kromannreumert.user.entity.User;
import com.example.kromannreumert.user.mapper.UserMapper;
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
import java.util.Optional;

@Service
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final LoggingService loggingService;
    private final static Logger log = LoggerFactory.getLogger(UserService.class);
    private final UserMapper userMapper;
    private final RoleService roleService;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, LoggingService loggingService, UserMapper userMapper, RoleService roleService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.loggingService = loggingService;
        this.userMapper = userMapper;
        this.roleService = roleService;
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

    public String createUser(User user, String name) {
        try {

            log.info("Trying to create user {}", user.getName());
            user.setPassword(passwordEncoder.encode(user.getPassword()));
            log.info("Successfully encrypted password {}", user.getName());
            userRepository.save(user);
            log.info("Successfully created {}", user.getName());
            loggingService.log(LogAction.CREATE_USER,name,"Created new user: " + user.getName());

            return "User created: " + user.getName();

        } catch (RuntimeException e) {

            log.error("Could not create user");
            loggingService.log(LogAction.CREATE_USER_FAILED,name,"Created new user failed: " + user.getName());
            throw new RuntimeException("Could not create user");

        }
    }

    public User findUserByUsername(String username) {
        log.info("Service: Trying to find user by username {}", username);
        return userRepository.findByUsername(username).orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }

    public List<UserResponseDTO>getAllUsers(String name){
        try {
            List<User> users = userRepository.findAll();

            List<UserResponseDTO> dtoList = users.stream()
                    .map(userMapper::toUserResponseDTO)
                    .toList();

            loggingService.log(LogAction.VIEW_ALL_USERS, name, "Viewed all users");
            return dtoList;
        } catch(RuntimeException e){
            loggingService.log(LogAction.VIEW_ALL_USERS_FAILED, name, "Failed to view all users");
            throw new RuntimeException("Could not load all users");
        }
    }

    public UserResponseDTO getUserByUserId(int id, String name){
        try{
            Optional<User>user = userRepository.findById(id);

            List<UserResponseDTO>dtoList = user.stream()
                    .map(userMapper::toUserResponseDTO).toList();

            loggingService.log(LogAction.VIEW_ONE_USER, name, "Viewed one user with id: " + id);
            return dtoList.getFirst();

        } catch(RuntimeException e){
            loggingService.log(LogAction.VIEW_ONE_USER_FAILED, name, "Failed to view one user with id: " + id);
            throw new RuntimeException("Could not load user" + id);
        }
    }

    public UserResponseDTO updateUser(int userId, UserRequestDTO userRequestDTO, String name){
        try {
            //laver vores request om til en optional, for at kunne stream den
            Optional<UserRequestDTO> dto = Optional.of(userRequestDTO);

            //laver vores request user om til en user
            User user = dto.stream()
                    .map(userMapper::toUser).toList().getFirst();

            //sætter user id så den bliver opdateret
            user.setUserId(Integer.valueOf(userId).longValue());

            //opdatere brugeren i db
            userRepository.save(user);

            //henter den nye bruger fra db
            Optional<User>getUserFromDb = userRepository.findById(userId);

            //laver ny bruger om til en response
            UserResponseDTO userResponse = getUserFromDb.stream().map(userMapper::toUserResponseDTO).toList().getFirst();

            //logger handlingerne
            loggingService.log(LogAction.UPDATE_USER, name, "Updated user with user id: " + userId + ", new user is" + user);

            //retunerer user
            return userResponse;
        }catch(RuntimeException e){
            loggingService.log(LogAction.UPDATE_USER_FAILED, name, "Failed to update user, with user id:" + userId);
            throw new RuntimeException("Could not update user");
        }
    }

    public void deleteUser(int userId, String name){
        try{
            userRepository.deleteById(userId);
            loggingService.log(LogAction.DELETE_USER, name, "Deleted user with user Id: " + userId);
        }catch(RuntimeException e){
            loggingService.log(LogAction.DELETE_USER_FAILED, name, "Failed to delete user, with user id: " + userId);
            throw new RuntimeException("Could not delete user: " + userId);
        }
    }

    public int getNumberOfAllUsers() {
        return userRepository.findAll().size();
    }


}
