package com.example.questionbank.controller;

import com.example.questionbank.model.ClassEntity;
import com.example.questionbank.service.ClassService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/class-entities")
public class ClassController {

    @Autowired
    private ClassService classEntityService;

    @PostMapping
    public ResponseEntity<ClassEntity> createClassEntity(@RequestBody ClassEntity classEntity) {
        ClassEntity createdClassEntity = classEntityService.createClassEntity(classEntity);
        return new ResponseEntity<>(createdClassEntity, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ClassEntity> updateClassEntity(@PathVariable Long id, @RequestBody ClassEntity classEntity) {
        ClassEntity updatedClassEntity = classEntityService.updateClassEntity(id, classEntity);
        return ResponseEntity.ok(updatedClassEntity);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteClassEntity(@PathVariable Long id) {
        classEntityService.deleteClassEntity(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<ClassEntity> getClassEntityById(@PathVariable Long id) {
        ClassEntity classEntity = classEntityService.getClassEntityById(id);
        return ResponseEntity.ok(classEntity);
    }

    @GetMapping
    public ResponseEntity<List<ClassEntity>> getAllClassEntities() {
        List<ClassEntity> classEntities = classEntityService.getAllClassEntities();
        return ResponseEntity.ok(classEntities);
    }

    @GetMapping("/search")
    public ResponseEntity<Page<ClassEntity>> searchClassEntities(@RequestParam String name, Pageable pageable) {
        Page<ClassEntity> classEntities = classEntityService.searchClassEntities(name, pageable);
        return ResponseEntity.ok(classEntities);
    }
}

