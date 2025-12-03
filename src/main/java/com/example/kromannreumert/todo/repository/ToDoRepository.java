package com.example.kromannreumert.todo.repository;

import com.example.kromannreumert.todo.entity.ToDo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ToDoRepository extends JpaRepository<ToDo, Long> {
    List<ToDo> findAllByArchivedFalse();

    List<ToDo> findDistinctByCaseId_Users_UsernameAndArchivedFalse(String username);

    List<ToDo> findDistinctByUsers_UsernameAndArchivedFalse(String username);
}
