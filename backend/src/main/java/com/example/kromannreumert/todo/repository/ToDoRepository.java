package com.example.kromannreumert.todo.repository;

import com.example.kromannreumert.todo.entity.ToDo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ToDoRepository extends JpaRepository<ToDo, Long> {
    List<ToDo> findAllByArchivedFalse();

    List<ToDo> findDistinctByCaseId_Users_UsernameAndArchivedFalse(String username);

    List<ToDo> findDistinctByUsers_UsernameAndArchivedFalse(String username);

    @Query("""
        SELECT DISTINCT t FROM ToDo t
        JOIN t.users a
        WHERE LOWER(a.username) LIKE LOWER(CONCAT('%', :q, '%'))
           OR LOWER(a.name)     LIKE LOWER(CONCAT('%', :q, '%'))
           OR LOWER(a.email)    LIKE LOWER(CONCAT('%', :q, '%'))
    """)
    List<ToDo> findByAssigneeSearch(@Param("q") String query);
}
