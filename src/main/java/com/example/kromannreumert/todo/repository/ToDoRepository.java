package com.example.kromannreumert.todo.repository;

import com.example.kromannreumert.todo.entity.ToDo;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ToDoRepository extends JpaRepository<ToDo, Long> {
}
