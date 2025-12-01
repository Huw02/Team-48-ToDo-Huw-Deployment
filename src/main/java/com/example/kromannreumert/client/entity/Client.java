package com.example.kromannreumert.client.entity;

import com.example.kromannreumert.user.entity.User;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.UniqueElements;

import java.util.Set;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Client {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Column(unique = true)
    private String name;

    @ManyToMany
    @JoinTable(
            name = "client_assignee",
            joinColumns = @JoinColumn(name = "client_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    public Set<User> users;

    // 99xxxx nummeret plus id;
    @NotNull
    @Column(unique = true)
    private Long IDPrefix;


}
