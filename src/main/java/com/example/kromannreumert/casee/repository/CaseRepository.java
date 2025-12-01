package com.example.kromannreumert.casee.repository;

import com.example.kromannreumert.casee.entity.Casee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CaseRepository extends JpaRepository<Casee, Long> {
    Optional<Casee> findAllByName(String name);
}
