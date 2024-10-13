package com.example.questionbank.repository;

import com.example.questionbank.model.Chapter;
import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChapterRepository extends JpaRepository<Chapter, Long> {

    Page<Chapter> findByNameContainingIgnoreCase(String name, Pageable pageable);

    @Query("SELECT c FROM Chapter c WHERE " +
            "(:subjectId IS NULL OR c.subject.id = :subjectId) AND " +
            "(:classId IS NULL OR c.subject.classEntity.id = :classId)")
    List<Chapter> findBySubjectAndClass(@Param("subjectId") Long subjectId,
                                        @Param("classId") Long classId);
}

