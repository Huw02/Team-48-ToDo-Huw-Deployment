package com.example.kromannreumert.logging.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Data
@Getter
@Setter
public class Logging {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String actor;

    @Enumerated(EnumType.STRING)
    private LogAction action;

    private String details;

    private LocalDateTime timestamp = LocalDateTime.now();

    public Logging() {}

    public Logging(String actor, LogAction action, String details) {
        this.actor = actor;
        this.action = action;
        this.details = details;
    }

    public void Logging(LogAction action, String actor, String details, Long oldPrefix) {

    }



    @Override
    public String toString() {
        return "Logging{" +
                "id=" + id +
                ", actor='" + actor + '\'' +
                ", action=" + action +
                ", details='" + details + '\'' +
                ", timestamp=" + timestamp +
                '}';
    }
}
