package com.example.questionbank.repository;

import com.example.questionbank.model.ClassEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ClassRepository extends JpaRepository<ClassEntity, Long> {

    /**
     * Find class by name
     */
    Optional<ClassEntity> findByName(String name);

    /**
     * Check if class exists by name
     */
    boolean existsByName(String name);


    // Search by name with pagination
    Page<ClassEntity> findByNameContainingIgnoreCase(String name, Pageable pageable);

    // Check if class exists by name
    boolean existsByNameIgnoreCase(String name);

    // Find by name (case insensitive)
    Optional<ClassEntity> findByNameIgnoreCase(String name);

    // Get only active classes
    List<ClassEntity> findByIsActiveTrue();

    // Get active classes with pagination
    Page<ClassEntity> findByIsActiveTrue(Pageable pageable);

    // Search active classes by name
    Page<ClassEntity> findByNameContainingIgnoreCaseAndIsActiveTrue(String name, Pageable pageable);

    // Custom query to get active classes ordered by name
    @Query("SELECT c FROM ClassEntity c WHERE c.isActive = true ORDER BY c.name")
    List<ClassEntity> findActiveClassesOrderByName();

    // Count total subjects for a class
    @Query("SELECT COUNT(s) FROM Subject s WHERE s.classEntity.id = :classId")
    Long countSubjectsByClassId(@Param("classId") Long classId);

    // Find classes with subject count
    @Query("SELECT c, COUNT(s) as subjectCount FROM ClassEntity c LEFT JOIN c.subjects s " +
            "WHERE c.isActive = true GROUP BY c.id ORDER BY c.name")
    List<Object[]> findActiveClassesWithSubjectCount();
}