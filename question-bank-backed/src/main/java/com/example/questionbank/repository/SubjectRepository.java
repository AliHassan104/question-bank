package com.example.questionbank.repository;

import com.example.questionbank.model.Subject;
import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SubjectRepository extends JpaRepository<Subject, Long> {

    /**
     * Find subject by name
     */
    Optional<Subject> findByName(String name);

    /**
     * Check if subject exists by name
     */
    boolean existsByName(String name);

    /**
     * Find subject by name (case insensitive)
     */
    Optional<Subject> findByNameIgnoreCase(String name);

    boolean existsByNameIgnoreCaseAndClassEntityId(String name, Long classId);

    List<Subject> findByIsActiveTrue();

    Page<Subject> findByIsActiveTrue(Pageable pageable);

    Page<Subject> findByNameContainingIgnoreCase(String name, Pageable pageable);

    List<Subject> findByClassEntityIdAndIsActiveTrue(Long classId);
}