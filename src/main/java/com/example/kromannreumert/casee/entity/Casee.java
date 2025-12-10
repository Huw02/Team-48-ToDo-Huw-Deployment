package com.example.kromannreumert.casee.entity;

import com.example.kromannreumert.client.entity.Client;
import com.example.kromannreumert.todo.entity.ToDo;
import com.example.kromannreumert.user.entity.User;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Casee {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @ManyToOne
    private Client client;

    @ManyToOne
    private User responsibleUser;

    @ManyToMany
    @JoinTable(
            name = "case_assignee",
            joinColumns = @JoinColumn(name = "case_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    private Set<User> users = new HashSet<>();

    @NotNull
    @Column(unique = true)
    public Long idPrefix;

    private LocalDateTime created;

    @JsonIgnore
    @OneToMany(mappedBy = "caseId", cascade = CascadeType.REMOVE)
    private List<ToDo> todos;


    public Casee(String name, Client client, Set<User> users, Long idPrefix, User responsibleUser, LocalDateTime created) {
        this.name = name;
        this.client = client;
        this.users = users;
        this.idPrefix = idPrefix;
        this.responsibleUser = responsibleUser;
        this.created = created;
    }

}
