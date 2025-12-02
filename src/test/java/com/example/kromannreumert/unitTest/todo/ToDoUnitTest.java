package com.example.kromannreumert.unitTest.todo;

import com.example.kromannreumert.logging.entity.LogAction;
import com.example.kromannreumert.logging.service.LoggingService;
import com.example.kromannreumert.todo.dto.ToDoRequestDto;
import com.example.kromannreumert.todo.dto.ToDoRequestNewToDoDto;
import com.example.kromannreumert.todo.dto.ToDoResponseDto;
import com.example.kromannreumert.todo.entity.Priority;
import com.example.kromannreumert.todo.entity.Status;
import com.example.kromannreumert.todo.entity.ToDo;
import com.example.kromannreumert.todo.mapper.ToDoMapper;
import com.example.kromannreumert.todo.repository.ToDoRepository;
import com.example.kromannreumert.todo.service.ToDoService;
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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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

    @Test
    void getToDoSize_returnsNumberOfTodos() {
        List<ToDo> toDos = List.of(new ToDo(), new ToDo(), new ToDo());
        when(toDoRepository.findAll()).thenReturn(toDos);

        int size = toDoService.getToDoSize();

        assertEquals(3, size);
        verify(toDoRepository).findAll();
    }

    @Test
    void findAll_returnsMappedDtosNotArchivedAndLogs() {
        String userName = "jurist";

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

        when(toDoRepository.findAllByArchivedFalse()).thenReturn(List.of(entity));
        when(toDoMapper.toToDoResponseDto(entity)).thenReturn(dto);

        List<ToDoResponseDto> result = toDoService.findAll(userName);

        assertEquals(1, result.size());
        assertEquals(dto, result.getFirst());

        verify(toDoRepository).findAllByArchivedFalse();
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
                LocalDate.of(2025, 12, 1),
                LocalDate.of(2025, 12, 2),
                Priority.MEDIUM
        );

        ToDo entityBeforeSave = new ToDo(
                null,
                request.name(),
                request.description(),
                null,
                LocalDateTime.now(),
                request.startDate(),
                request.endDate(),
                request.priority(),
                Status.NOT_STARTED,
                false,
                Set.of()
        );

        ToDo entityAfterSave = new ToDo(
                1L,
                request.name(),
                request.description(),
                null,
                entityBeforeSave.getCreated(),
                request.startDate(),
                request.endDate(),
                request.priority(),
                Status.NOT_STARTED,
                false,
                Set.of()
        );

        ToDoResponseDto responseDto = new ToDoResponseDto(
                1L,
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

        when(toDoMapper.toToDo(request)).thenReturn(entityBeforeSave);
        when(toDoRepository.save(entityBeforeSave)).thenReturn(entityAfterSave);
        when(toDoMapper.toToDoResponseDto(entityAfterSave)).thenReturn(responseDto);

        ToDoResponseDto result = toDoService.createToDo(userName, request);

        assertEquals(responseDto, result);
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
}
