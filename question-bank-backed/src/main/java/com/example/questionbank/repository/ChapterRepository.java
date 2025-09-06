package com.example.questionbank.repository;

import com.example.questionbank.model.Chapter;
import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ChapterRepository extends JpaRepository<Chapter, Long> {

    /**
     * Find chapter by name
     */
    Optional<Chapter> findByName(String name);

    /**
     * Check if chapter exists by name
     */
    boolean existsByName(String name);

    /**
     * Find chapter by name (case insensitive)
     */
    Optional<Chapter> findByNameIgnoreCase(String name);

    /**
     * Find chapters by subject id
     */
    List<Chapter> findBySubjectId(Long subjectId);

    Page<Chapter> findByNameContainingIgnoreCase(String name, Pageable pageable);

    @Query("SELECT c FROM Chapter c WHERE " +
            "(:subjectId IS NULL OR c.subject.id = :subjectId) AND " +
            "(:classId IS NULL OR c.subject.classEntity.id = :classId)")
    List<Chapter> findBySubjectAndClass(@Param("subjectId") Long subjectId,
                                        @Param("classId") Long classId);

    boolean existsByNameIgnoreCaseAndSubjectId(String name, Long subjectId);
    List<Chapter> findByIsActiveTrue();
    Page<Chapter> findByIsActiveTrue(Pageable pageable);
    List<Chapter> findBySubjectIdAndIsActiveTrue(Long subjectId);
    List<Chapter> findBySubjectClassEntityIdAndIsActiveTrue(Long classId);

    @Query("SELECT c FROM Chapter c WHERE " +
            "(:subjectId IS NULL OR c.subject.id = :subjectId) AND " +
            "(:classId IS NULL OR c.subject.classEntity.id = :classId) AND " +
            "c.isActive = true")
    List<Chapter> findActiveChaptersBySubjectAndClass(@Param("subjectId") Long subjectId,
                                                      @Param("classId") Long classId);


}

