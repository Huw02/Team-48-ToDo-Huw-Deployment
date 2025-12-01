package com.example.kromannreumert.todo.entity;

import com.example.kromannreumert.casee.entity.Casee;
import com.example.kromannreumert.user.entity.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.Date;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Set;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class ToDo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private String description;

    @ManyToOne
    private Casee caseId;

    private LocalDateTime created;

    private LocalDate startDate;

    private LocalDate endDate;

    @Enumerated(EnumType.STRING)
    private Priority priority;

    @Enumerated(EnumType.STRING)
    private Status status;

    private Boolean archived;

    @ManyToMany
    @JoinTable(
            name = "todo_assignee",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "todo_id")
    )
    private Set<User> users;

    public ToDo(String name, String description, LocalDateTime created, LocalDate startDate, LocalDate endDate, Priority priority, Status status, Boolean archived) {
        this.name = name;
        this.description = description;
        this.created = created;
        this.startDate = startDate;
        this.endDate = endDate;
        this.priority = priority;
        this.status = status;
        this.archived = archived;
    }
}
