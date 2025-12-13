package com.example.kromannreumert.unitTest.logging;

import com.example.kromannreumert.logging.entity.LogAction;
import com.example.kromannreumert.logging.entity.Logging;
import com.example.kromannreumert.logging.repository.LogRepository;
import com.example.kromannreumert.logging.service.LoggingService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.security.Principal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
public class LoggingUnitTest {

    @InjectMocks
    LoggingService loggingService;

    @Mock
    LogRepository logRepository;

    @Test
    void getAllLogs() {
        Principal principal = mock(Principal.class);
        when(principal.getName()).thenReturn("testuser");

        // ARRANGE
        List<Logging> addData = List.of(new Logging("Zahaa", LogAction.CREATE_USER, "user created"));
        when(logRepository.findAll()).thenReturn(addData);

        // ACT
        List<Logging> result = loggingService.getAllLogs(principal.getName());

        // ASSERT
        assertNotNull(result);
        assertFalse(result.isEmpty());
        verify(logRepository).findAll();

    }

    @Test
    void addLogs() {

        // ARRANGE
        String actor = "Zahaa";
        LogAction action = LogAction.CREATE_TASK;
        String details = "Task created by: " + actor;

        // ACT
        ArgumentCaptor<Logging> logsObject = ArgumentCaptor.forClass(Logging.class);
        loggingService.log(action,actor, details);
        verify(logRepository).save(logsObject.capture());
        Logging loggedContext = logsObject.getValue();

        // ASSERT
        assertEquals(actor, loggedContext.getActor());
        assertEquals(action, loggedContext.getAction());
        assertEquals(details, loggedContext.getDetails());

    }

    @Test
    void getAllLogsByAction() {

        // ARRANGE
        String actor = "Zahaa";
        LogAction createTaskAction = LogAction.CREATE_TASK;
        String createTask = "Task created";
        List<Logging> addData = List.of(new Logging(actor, createTaskAction, createTask));

        // ACT
        when(logRepository.findAllByAction(createTaskAction)).thenReturn(addData);
        List<Logging> result = loggingService.getAllLogsByAction(createTaskAction);

        // ASSERT
        assertEquals(1, result.size());
        assertNotNull(result);
        assertEquals(LogAction.CREATE_TASK, result.getFirst().getAction());
        verify(logRepository).findAllByAction(createTaskAction);

    }

}
