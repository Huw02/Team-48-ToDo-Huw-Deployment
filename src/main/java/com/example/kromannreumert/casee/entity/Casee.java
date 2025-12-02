package com.example.kromannreumert.casee.entity;

import com.example.kromannreumert.client.entity.Client;
import com.example.kromannreumert.user.entity.User;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashSet;
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

    public Casee(String name, Client client, Set<User> users, Long idPrefix, User responsibleUser) {
        this.name = name;
        this.client = client;
        this.users = users;
        this.idPrefix = idPrefix;
        this.responsibleUser = responsibleUser;
    }

}
