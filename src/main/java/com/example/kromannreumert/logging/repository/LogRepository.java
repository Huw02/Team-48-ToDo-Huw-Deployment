package com.example.kromannreumert.logging.repository;

import com.example.kromannreumert.logging.entity.LogAction;
import com.example.kromannreumert.logging.entity.Logging;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LogRepository extends JpaRepository<Logging, Integer> {

    List<Logging> findAllByAction(LogAction logAction);
}
