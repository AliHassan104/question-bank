package com.example.questionbank.repository;

import com.example.questionbank.model.Subject;
import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SubjectRepository extends JpaRepository<Subject, Long> {
    List<Subject> findByNameContainingIgnoreCase(String name);

    List<Subject> findByClassEntityId(Long classId);
}