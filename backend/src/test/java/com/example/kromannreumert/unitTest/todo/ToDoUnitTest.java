package com.example.kromannreumert.unitTest.todo;

import com.example.kromannreumert.casee.entity.Casee;
import com.example.kromannreumert.casee.repository.CaseRepository;
import com.example.kromannreumert.exception.customException.http4xxExceptions.toDo.ToDoNotFoundException;
import com.example.kromannreumert.logging.entity.LogAction;
import com.example.kromannreumert.logging.service.LoggingService;
import com.example.kromannreumert.todo.dto.ToDoAssigneeUpdateRequest;
import com.example.kromannreumert.todo.dto.ToDoRequestDto;
import com.example.kromannreumert.todo.dto.ToDoRequestNewToDoDto;
import com.example.kromannreumert.todo.dto.ToDoResponseDto;
import com.example.kromannreumert.todo.entity.Priority;
import com.example.kromannreumert.todo.entity.Status;
import com.example.kromannreumert.todo.entity.ToDo;
import com.example.kromannreumert.todo.mapper.ToDoMapper;
import com.example.kromannreumert.todo.repository.ToDoRepository;
import com.example.kromannreumert.todo.service.ToDoService;
import com.example.kromannreumert.user.entity.Role;
import com.example.kromannreumert.user.entity.User;
import com.example.kromannreumert.user.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ToDoUnitTest {

    @InjectMocks
    ToDoService toDoService;

    @Mock
    ToDoRepository toDoRepository;

    @Mock
    ToDoMapper toDoMapper;

    @Mock
    LoggingService loggingService;

    @Mock
    UserRepository userRepository;

    @Mock
    CaseRepository caseRepository;

    @Test
    void getToDoSize_returnsNumberOfTodos() {
        List<ToDo> toDos = List.of(new ToDo(), new ToDo(), new ToDo());
        when(toDoRepository.findAll()).thenReturn(toDos);

        int size = toDoService.getToDoSize();

        assertEquals(3, size);
        verify(toDoRepository).findAll();
    }

    @Test
    void findAll_asAdmin_usesFindAllByArchivedFalse_andLogs() {
        String userName = "admin";

        Role adminRole = new Role(1L, "ADMIN");
        User admin = User.builder()
                .username(userName)
                .name("Admin User")
                .email("admin@example.com")
                .password("secret")
                .roles(Set.of(adminRole))
                .build();

        ToDo entity = new ToDo(
                1L,
                "test",
                "beskrivelse",
                null,
                LocalDateTime.now(),
                LocalDate.now(),
                LocalDate.now().plusDays(1),
                Priority.MEDIUM,
                Status.NOT_STARTED,
                false,
                Set.of()
        );

        ToDoResponseDto dto = new ToDoResponseDto(
                1L,
                "test",
                "beskrivelse",
                entity.getCreated(),
                entity.getStartDate(),
                entity.getEndDate(),
                entity.getUsers(),
                entity.getPriority(),
                entity.getStatus(),
                entity.getArchived()
        );

        when(userRepository.findByUsername(userName)).thenReturn(Optional.of(admin));
        when(toDoRepository.findAllByArchivedFalse()).thenReturn(List.of(entity));
        when(toDoMapper.toToDoResponseDto(entity)).thenReturn(dto);

        List<ToDoResponseDto> result = toDoService.findAll(userName);

        assertEquals(1, result.size());
        assertEquals(dto, result.getFirst());

        verify(userRepository).findByUsername(userName);
        verify(toDoRepository).findAllByArchivedFalse();
        verify(toDoRepository, never()).findDistinctByCaseId_Users_UsernameAndArchivedFalse(anyString());
        verify(toDoMapper).toToDoResponseDto(entity);
        verify(loggingService).log(eq(LogAction.VIEW_ALL_TODOS), eq(userName), anyString());
    }

    @Test
    void findAll_asJurist_usesCaseAssigneeQuery_andLogs() {
        String userName = "jurist";

        Role juristRole = new Role(4L, "JURIST");
        User jurist = User.builder()
                .username(userName)
                .name("Jurist User")
                .email("jurist@example.com")
                .password("secret")
                .roles(Set.of(juristRole))
                .build();

        ToDo entity = new ToDo(
                1L,
                "test",
                "beskrivelse",
                null,
                LocalDateTime.now(),
                LocalDate.now(),
                LocalDate.now().plusDays(1),
                Priority.MEDIUM,
                Status.NOT_STARTED,
                false,
                Set.of()
        );

        ToDoResponseDto dto = new ToDoResponseDto(
                1L,
                "test",
                "beskrivelse",
                entity.getCreated(),
                entity.getStartDate(),
                entity.getEndDate(),
                entity.getUsers(),
                entity.getPriority(),
                entity.getStatus(),
                entity.getArchived()
        );

        when(userRepository.findByUsername(userName)).thenReturn(Optional.of(jurist));
        when(toDoRepository.findDistinctByCaseId_Users_UsernameAndArchivedFalse(userName))
                .thenReturn(List.of(entity));
        when(toDoMapper.toToDoResponseDto(entity)).thenReturn(dto);

        List<ToDoResponseDto> result = toDoService.findAll(userName);

        assertEquals(1, result.size());
        assertEquals(dto, result.getFirst());

        verify(userRepository).findByUsername(userName);
        verify(toDoRepository).findDistinctByCaseId_Users_UsernameAndArchivedFalse(userName);
        verify(toDoRepository, never()).findAllByArchivedFalse();
        verify(toDoMapper).toToDoResponseDto(entity);
        verify(loggingService).log(eq(LogAction.VIEW_ALL_TODOS), eq(userName), anyString());
    }

    @Test
    void findToDoById_returnsDtoWhenExists() {
        String userName = "jurist";
        Long id = 1L;

        ToDo entity = new ToDo();
        entity.setId(id);
        entity.setName("test");
        entity.setDescription("beskrivelse");

        ToDoResponseDto dto = new ToDoResponseDto(
                id,
                "test",
                "beskrivelse",
                LocalDateTime.now(),
                LocalDate.now(),
                LocalDate.now().plusDays(1),
                Set.of(),
                Priority.LOW,
                Status.NOT_STARTED,
                false
        );

        when(toDoRepository.findById(id)).thenReturn(Optional.of(entity));
        when(toDoMapper.toToDoResponseDto(entity)).thenReturn(dto);

        ToDoResponseDto result = toDoService.findToDoById(userName, id);

        assertEquals(dto, result);
        verify(toDoRepository).findById(id);
        verify(toDoMapper).toToDoResponseDto(entity);
        verify(loggingService).log(eq(LogAction.VIEW_ONE_TODO), eq(userName), anyString());
    }

    @Test
    void createToDo_savesEntityAndReturnsDto() {
        String userName = "jurist";

        ToDoRequestNewToDoDto request = new ToDoRequestNewToDoDto(
                "ny todo",
                "beskrivelse",
                1L,
                LocalDate.of(2025, 12, 1),
                LocalDate.of(2025, 12, 2),
                Priority.MEDIUM
        );

        Casee casee = new Casee();
        casee.setId(1L);

        ToDo entityBeforeSave = new ToDo();
        entityBeforeSave.setName(request.name());
        entityBeforeSave.setDescription(request.description());
        entityBeforeSave.setStartDate(request.startDate());
        entityBeforeSave.setEndDate(request.endDate());
        entityBeforeSave.setPriority(request.priority());
        entityBeforeSave.setStatus(Status.NOT_STARTED);
        entityBeforeSave.setArchived(false);

        ToDo entityAfterSave = new ToDo();
        entityAfterSave.setId(10L);
        entityAfterSave.setName(request.name());
        entityAfterSave.setDescription(request.description());
        entityAfterSave.setStartDate(request.startDate());
        entityAfterSave.setEndDate(request.endDate());
        entityAfterSave.setPriority(request.priority());
        entityAfterSave.setStatus(Status.NOT_STARTED);
        entityAfterSave.setArchived(false);
        entityAfterSave.setCaseId(casee);

        ToDoResponseDto responseDto = new ToDoResponseDto(
                10L,
                request.name(),
                request.description(),
                entityAfterSave.getCreated(),
                request.startDate(),
                request.endDate(),
                Set.of(),
                request.priority(),
                Status.NOT_STARTED,
                false
        );

        when(caseRepository.findById(1L)).thenReturn(Optional.of(casee));
        when(toDoMapper.toToDo(request)).thenReturn(entityBeforeSave);
        when(toDoRepository.save(entityBeforeSave)).thenReturn(entityAfterSave);
        when(toDoMapper.toToDoResponseDto(entityAfterSave)).thenReturn(responseDto);

        ToDoResponseDto result = toDoService.createToDo(userName, request);

        assertEquals(responseDto, result);
        assertEquals(casee, entityAfterSave.getCaseId());

        verify(caseRepository).findById(1L);
        verify(toDoMapper).toToDo(request);
        verify(toDoRepository).save(entityBeforeSave);
        verify(toDoMapper).toToDoResponseDto(entityAfterSave);
        verify(loggingService).log(eq(LogAction.CREATE_TODO), eq(userName), anyString());
    }

    @Test
    void deleteToDo_deletesEntityAndLogs() {
        String userName = "jurist";
        Long id = 1L;

        ToDo entity = new ToDo();
        entity.setId(id);
        entity.setName("test");

        when(toDoRepository.findById(id)).thenReturn(Optional.of(entity));

        toDoService.deleteTodo(userName, id);

        verify(toDoRepository).findById(id);
        verify(toDoRepository).delete(entity);
        verify(loggingService).log(eq(LogAction.DELETE_TODO), eq(userName), anyString());
    }

    @Test
    void updateToDo_updatesToDoAndReturnsDto() {
        String userName = "jurist";
        Long id = 1L;

        ToDoRequestDto request = new ToDoRequestDto(
                "updated",
                "opdateret beskrivelse",
                LocalDate.of(2025, 12, 1),
                LocalDate.of(2025, 12, 2),
                Set.of(),
                Priority.HIGH,
                Status.IN_PROGRESS,
                false
        );

        ToDo existing = new ToDo();
        existing.setId(id);
        existing.setName("old");
        existing.setDescription("old description");

        ToDoResponseDto responseDto = new ToDoResponseDto(
                id,
                request.name(),
                request.description(),
                LocalDateTime.now(),
                request.startDate(),
                request.endDate(),
                request.toDoAssignees(),
                request.priority(),
                request.status(),
                request.archived()
        );

        when(toDoRepository.findById(id)).thenReturn(Optional.of(existing));
        when(toDoRepository.save(existing)).thenReturn(existing);
        when(toDoMapper.toToDoResponseDto(existing)).thenReturn(responseDto);

        ToDoResponseDto result = toDoService.updateTodo(id, userName, request);

        assertEquals(responseDto, result);
        assertEquals("updated", existing.getName());
        assertEquals("opdateret beskrivelse", existing.getDescription());
        assertEquals(request.startDate(), existing.getStartDate());
        assertEquals(request.endDate(), existing.getEndDate());
        assertEquals(request.priority(), existing.getPriority());
        assertEquals(request.status(), existing.getStatus());
        assertEquals(request.archived(), existing.getArchived());

        verify(toDoRepository).findById(id);
        verify(toDoRepository).save(existing);
        verify(toDoMapper).toToDoResponseDto(existing);
        verify(loggingService).log(eq(LogAction.UPDATE_TODO), eq(userName), anyString());
    }

    @Test
    void findAssignedToUser_returnsMappedDtos() {
        String userName = "jurist01";

        ToDo entity = new ToDo(
                1L,
                "NDA",
                "Draft",
                null,
                LocalDateTime.now(),
                LocalDate.now(),
                LocalDate.now().plusDays(1),
                Priority.HIGH,
                Status.NOT_STARTED,
                false,
                Set.of()
        );

        ToDoResponseDto dto = new ToDoResponseDto(
                1L,
                "NDA",
                "Draft",
                entity.getCreated(),
                entity.getStartDate(),
                entity.getEndDate(),
                entity.getUsers(),
                entity.getPriority(),
                entity.getStatus(),
                entity.getArchived()
        );

        when(toDoRepository.findDistinctByUsers_UsernameAndArchivedFalse(userName))
                .thenReturn(List.of(entity));
        when(toDoMapper.toToDoResponseDto(entity)).thenReturn(dto);

        List<ToDoResponseDto> result = toDoService.findAssignedToUser(userName);

        assertEquals(1, result.size());
        assertEquals(dto, result.getFirst());

        verify(toDoRepository).findDistinctByUsers_UsernameAndArchivedFalse(userName);
        verify(toDoMapper).toToDoResponseDto(entity);

        verifyNoInteractions(loggingService);
    }

    @Test
    void findAssignedToUser_wrapsExceptionsInRuntimeException() {
        String userName = "jurist01";

        when(toDoRepository.findDistinctByUsers_UsernameAndArchivedFalse(userName))
                .thenThrow(new RuntimeException("DB error"));

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> toDoService.findAssignedToUser(userName));

        assertEquals("Failed fetching assigned todos", ex.getMessage());
        assertEquals("DB error", ex.getCause().getMessage());

        verify(toDoRepository).findDistinctByUsers_UsernameAndArchivedFalse(userName);
        verifyNoInteractions(toDoMapper, loggingService);
    }

    @Test
    void updateAssignees_replacesAssignees_andLogsAddedUsers() {
        Long todoId = 1L;
        String userName = "jurist";

        User existingUser = User.builder()
                .userId(1L)
                .username("worker1")
                .build();

        ToDo todo = new ToDo();
        todo.setId(todoId);
        todo.setName("Udarbejd kontrakt");
        todo.setUsers(Set.of(existingUser));

        ToDoAssigneeUpdateRequest request = new ToDoAssigneeUpdateRequest(
                List.of(1L, 2L)
        );

        User user1 = existingUser;
        User user2 = User.builder()
                .userId(2L)
                .username("worker2")
                .build();

        ToDo savedTodo = todo;

        ToDoResponseDto responseDto = new ToDoResponseDto(
                todoId,
                todo.getName(),
                "beskrivelse",
                LocalDateTime.now(),
                LocalDate.now(),
                LocalDate.now().plusDays(1),
                Set.of(user1, user2),
                Priority.MEDIUM,
                Status.NOT_STARTED,
                false
        );

        when(toDoRepository.findById(todoId)).thenReturn(Optional.of(todo));
        when(userRepository.findById(1)).thenReturn(Optional.of(user1));
        when(userRepository.findById(2)).thenReturn(Optional.of(user2));
        when(toDoRepository.save(todo)).thenReturn(savedTodo);
        when(toDoMapper.toToDoResponseDto(savedTodo)).thenReturn(responseDto);

        ToDoResponseDto result = toDoService.updateAssignees(todoId, request, userName);

        assertEquals(responseDto, result);
        assertEquals(Set.of(user1, user2), todo.getUsers());

        verify(toDoRepository).findById(todoId);
        verify(userRepository).findById(1);
        verify(userRepository).findById(2);
        verify(toDoRepository).save(todo);
        verify(toDoMapper).toToDoResponseDto(savedTodo);

        verify(loggingService).log(eq(LogAction.ADDED_USERS_TO_TODO), eq(userName),
                contains("Assigned users to: " + todo.getName()));

        verify(loggingService, never()).log(eq(LogAction.REMOVED_USERS_TO_TODO), anyString(), anyString());
    }

    @Test
    void getCaseAssigneesForTodo_returnsUsersFromCase() {
        Long todoId = 1L;

        User user1 = User.builder()
                .userId(1L)
                .username("worker1")
                .name("Worker One")
                .build();

        User user2 = User.builder()
                .userId(2L)
                .username("worker2")
                .name("Worker Two")
                .build();

        Casee casee = new Casee();
        casee.setId(100L);
        casee.setUsers(Set.of(user1, user2));

        ToDo todo = new ToDo();
        todo.setId(todoId);
        todo.setName("Udarbejd kontrakt");
        todo.setCaseId(casee);

        when(toDoRepository.findById(todoId)).thenReturn(Optional.of(todo));

        Set<User> result = toDoService.getCaseAssigneesForTodo(todoId);

        assertEquals(Set.of(user1, user2), result);
        assertEquals(2, result.size());

        verify(toDoRepository).findById(todoId);
        verifyNoInteractions(userRepository, caseRepository, toDoMapper, loggingService);
    }

    @Test
    void getCaseAssigneesForTodo_throwsWhenTodoNotFound() {
        Long todoId = 99L;

        when(toDoRepository.findById(todoId)).thenReturn(Optional.empty());

        assertThrows(ToDoNotFoundException.class,
                () -> toDoService.getCaseAssigneesForTodo(todoId));

        verify(toDoRepository).findById(todoId);
        verifyNoInteractions(userRepository, caseRepository, toDoMapper, loggingService);
    }
}
