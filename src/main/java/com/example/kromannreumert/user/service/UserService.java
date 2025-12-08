package com.example.kromannreumert.user.service;

import com.example.kromannreumert.logging.entity.LogAction;
import com.example.kromannreumert.logging.service.LoggingService;
import com.example.kromannreumert.exception.customException.http4xxExceptions.ApiBusinessException;
import com.example.kromannreumert.exception.customException.http4xxExceptions.UserNotFoundException;
import com.example.kromannreumert.exception.customException.http5xxException.ActionFailedException;
import com.example.kromannreumert.user.dto.UserRequestDTO;
import com.example.kromannreumert.user.dto.UserResponseDTO;
import com.example.kromannreumert.user.entity.Role;
import com.example.kromannreumert.user.entity.User;
import com.example.kromannreumert.user.mapper.UserMapper;
import com.example.kromannreumert.user.repository.RoleRepository;
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
import java.util.Set;

@Service
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final LoggingService loggingService;
    private final static Logger log = LoggerFactory.getLogger(UserService.class);
    private final UserMapper userMapper;
    private final RoleService roleService;
    private final RoleRepository roleRepository;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, LoggingService loggingService, UserMapper userMapper, RoleService roleService, RoleRepository roleRepository) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.loggingService = loggingService;
        this.userMapper = userMapper;
        this.roleService = roleService;
        this.roleRepository = roleRepository;
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

    public UserResponseDTO createUser(UserRequestDTO userRequestDTO, String performedAct){
        try {
            Optional<UserRequestDTO> request = Optional.of(userRequestDTO);

            User user = request.stream().
                    map(userMapper::toUser).
                    toList().getFirst();

            User userFromDb = userRepository.save(user);

            UserResponseDTO response = userMapper.toUserResponseDTO(userFromDb);

            loggingService.log(LogAction.CREATE_USER, performedAct, "Created a user: " + response);

            return response;
        }catch(RuntimeException e){
            loggingService.log(LogAction.CREATE_USER_FAILED, performedAct, "Tried to create a user: " + userRequestDTO);
            throw new RuntimeException("Failed to create a new user");
        }
    }

    public User findUserByUsername(String username) {
        log.info("Service: Trying to find user by username {}", username);
        return userRepository.findByUsername(username).orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }

    public UserResponseDTO getUserByUsername(String username){
        try {
            Optional<User> user = userRepository.findByUsername(username);

            return user.stream()
                    .map(userMapper::toUserResponseDTO)
                    .toList().getFirst();

        } catch(RuntimeException e){
            if (e instanceof ApiBusinessException) throw e;
            throw new ActionFailedException(LogAction.VIEW_ONE_USER_FAILED, username, e);
        }
    }

    public List<UserResponseDTO>getAllUsers(String name){
        try {
            List<User> users = userRepository.findAll();

            List<UserResponseDTO> dtoList = users.stream()
                    .map(userMapper::toUserResponseDTO)
                    .toList();

            loggingService.log(LogAction.VIEW_ALL_USERS, name, "Viewed all users, the user found: " + dtoList.size() + " users in the system");
            return dtoList;
        } catch(RuntimeException e){
            loggingService.log(LogAction.VIEW_ALL_USERS_FAILED, name, "Failed to view all users");
            if (e instanceof ApiBusinessException) throw e;
            throw new ActionFailedException(LogAction.VIEW_ALL_USERS_FAILED, name, e);
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
            if (e instanceof ApiBusinessException) throw e;
            throw new ActionFailedException(LogAction.VIEW_ONE_USER_FAILED, name, e);
        }
    }

    public UserResponseDTO updateUser(int userId, UserRequestDTO dto, String name) {
        try {
            User userToUpdate = userRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            if (dto.username() != null)
                userToUpdate.setUsername(dto.username());

            if (dto.name() != null)
                userToUpdate.setName(dto.name());

            if (dto.email() != null)
                userToUpdate.setEmail(dto.email());

            if (dto.password() != null)
                userToUpdate.setPassword(passwordEncoder.encode(dto.password()));

            if (dto.roleId() != 0) {
                Role role = roleRepository.findById(dto.roleId())
                        .orElseThrow(() -> new RuntimeException("Role not found"));
                userToUpdate.setRoles(new java.util.HashSet<>(Set.of(role)));
            }
            userRepository.save(userToUpdate);

            loggingService.log(
                    LogAction.UPDATE_USER,
                    name,
                    "Updated user with user id: " + userId + ", new user is " + userToUpdate
            );
            return userMapper.toUserResponseDTO(userToUpdate);

        } catch(RuntimeException e){
            loggingService.log(LogAction.UPDATE_USER_FAILED, name, "Failed to update user, with user id:" + userId);
            if (e instanceof ApiBusinessException) throw e;
            throw new ActionFailedException(LogAction.UPDATE_USER_FAILED, name, e);
        }
    }

    public void deleteUser(int userId, String name){
        try{
            userRepository.deleteById(userId);
            loggingService.log(LogAction.DELETE_USER, name, "Deleted user with user Id: " + userId);
        }catch(RuntimeException e){
            loggingService.log(LogAction.DELETE_USER_FAILED, name, "Failed to delete user, with user id: " + userId);
            throw new ActionFailedException(LogAction.DELETE_USER_FAILED, name, e);
        }
    }

    public int getNumberOfAllUsers() {
        return userRepository.findAll().size();
    }


}
