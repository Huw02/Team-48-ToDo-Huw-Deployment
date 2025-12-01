package com.example.kromannreumert.todo.service;

import com.example.kromannreumert.logging.entity.LogAction;
import com.example.kromannreumert.logging.service.LoggingService;
import com.example.kromannreumert.todo.dto.ToDoRequestDto;
import com.example.kromannreumert.todo.dto.ToDoRequestNewToDoDto;
import com.example.kromannreumert.todo.dto.ToDoResponseDto;
import com.example.kromannreumert.todo.entity.ToDo;
import com.example.kromannreumert.todo.mapper.ToDoMapper;
import com.example.kromannreumert.todo.repository.ToDoRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ToDoService {

    private final ToDoRepository toDoRepository;
    private final ToDoMapper toDoMapper;
    private final LoggingService loggingService;

    public ToDoService(ToDoRepository toDoRepository, ToDoMapper toDoMapper, LoggingService loggingService) {
        this.toDoRepository = toDoRepository;
        this.toDoMapper = toDoMapper;
        this.loggingService = loggingService;
    }

    public int getToDoSize() {
        List<ToDo> getAll = toDoRepository.findAll();
        return getAll.size();
    }

    public List<ToDoResponseDto> findAll(String name) {
        try {
            List<ToDo> toDos = toDoRepository.findAllByArchivedFalse();

            List<ToDoResponseDto> responseDtos = toDos.stream()
                    .map(toDoMapper::toToDoResponseDto)
                    .toList();

            loggingService.log(LogAction.VIEW_ALL_TODOS, name, "Viewed all todos");

            return responseDtos;
        } catch (Exception e) {
            loggingService.log(LogAction.VIEW_ALL_TODOS_FAILED, name, "Failed to view all todos");

            throw new RuntimeException("Failed fetching todos", e);
        }

    }

    public ToDoResponseDto findToDoById(String name, Long id) {
        try {
            ToDo toDo = toDoRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Todo not found with id: " + id));

            ToDoResponseDto responseDto = toDoMapper.toToDoResponseDto(toDo);

            loggingService.log(LogAction.VIEW_ONE_TODO, name, "Viewed todo: " + toDo.getName());

            return responseDto;
        } catch (Exception e) {
            loggingService.log(LogAction.VIEW_ONE_TODO_FAILED, name, "Failed to view todo with id: " + id);

            throw new RuntimeException("Todo not found with id: " + id, e);
        }
    }

    public ToDoResponseDto createToDo(String name, ToDoRequestNewToDoDto todoRequestDto) {
        try {
            ToDo toDo = toDoMapper.toToDo(todoRequestDto);
            toDo = toDoRepository.save(toDo);

            loggingService.log(LogAction.CREATE_TODO, name, "Created a todo: " + toDo.getName());

            return toDoMapper.toToDoResponseDto(toDo);
        } catch (RuntimeException e) {
            loggingService.log(LogAction.CREATE_TODO_FAILED, name, "Failed to create todo: " + todoRequestDto.name());
            throw new RuntimeException("Could not create todo", e);
        }
    }

    public void deleteTodo(String name, Long id) {
        try {
            ToDo toDo = toDoRepository.findById(id)
                            .orElseThrow(() -> new RuntimeException("Could not find todo with id: " + id));

            toDoRepository.delete(toDo);

            loggingService.log(LogAction.DELETE_TODO, name, "Deleted todo: " + toDo.getName() + ", id: " + id);
        } catch (Exception e) {
            loggingService.log(LogAction.DELETE_TODO_FAILED, name, "Failed to delete todo with id: " + id + " " + e.getMessage());
            throw new RuntimeException("Could not delete todo with id: " + id, e);
        }
    }

    public ToDoResponseDto updateTodo(Long id, String name, ToDoRequestDto todoRequestDto) {
        try {
            ToDo todo = toDoRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Could not find todo with id: " + id));

            todo.setName(todoRequestDto.name());
            todo.setDescription(todoRequestDto.description());
            todo.setStartDate(todoRequestDto.startDate());
            todo.setEndDate(todoRequestDto.endDate());
            todo.setUsers(todoRequestDto.toDoAssignees());
            todo.setPriority(todoRequestDto.priority());
            todo.setStatus(todoRequestDto.status());
            todo.setArchived(todoRequestDto.archived());

            toDoRepository.save(todo);
            loggingService.log(LogAction.UPDATE_TODO, name, "Updated todo: " + todoRequestDto.name());

            return toDoMapper.toToDoResponseDto(todo);

        } catch (Exception e) {
            loggingService.log(LogAction.UPDATE_TODO_FAILED, name, "Failed to update todo: " + todoRequestDto.name() + " " + e.getMessage());
            throw new RuntimeException("Could not update todo: " + todoRequestDto.name(), e);
        }
    }
}
