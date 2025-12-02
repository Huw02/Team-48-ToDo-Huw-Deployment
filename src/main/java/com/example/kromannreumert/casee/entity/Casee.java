package com.example.kromannreumert.casee.entity;

import com.example.kromannreumert.client.entity.Client;
import com.example.kromannreumert.user.entity.User;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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
    @JoinColumn(name = "responsible_user_id")
    private User responsibleUser;

    @ManyToMany
    @JoinTable(
            name = "case_assignee",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "case_id")
    )
    public Set<User> users;

    @NotNull
    @Column(unique = true)
    public Long idPrefix;

    public Casee(String name, Client client, Set<User> users, Long idPrefix) {
        this.name = name;
        this.client = client;
        this.users = users;
        this.idPrefix = idPrefix;
    }

}
