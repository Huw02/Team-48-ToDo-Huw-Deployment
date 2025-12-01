package com.example.kromannreumert.unitTest.user;

import com.example.kromannreumert.user.repository.UserRepository;
import com.example.kromannreumert.user.service.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class UserUnitTest {


    @InjectMocks
    UserService userService;


    @Mock
    UserRepository userRepository;



    @Test
    void createUser(){

    }


    @Test
    void findUserByUsername(){


    }


    @Test
    void getAllUsers(){

    }


    @Test
    void getUserByUserId(){

    }


    @Test
    void updateUser(){

    }


    @Test
    void deleteUser(){

    }







}
