package com.example.questionbank.service.impl;

import com.example.questionbank.model.ClassEntity;
import com.example.questionbank.repository.ClassRepository;
import com.example.questionbank.service.ClassService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class ClassServiceImplementation implements ClassService {

    @Autowired
    private ClassRepository classEntityRepository;

    @Override
    public ClassEntity createClassEntity(ClassEntity classEntity) {
        return classEntityRepository.save(classEntity);
    }

    @Override
    public ClassEntity updateClassEntity(Long id, ClassEntity updatedClassEntity) {
        Optional<ClassEntity> existingClassEntityOpt = classEntityRepository.findById(id);
        if (existingClassEntityOpt.isPresent()) {
            ClassEntity existingClassEntity = existingClassEntityOpt.get();
            existingClassEntity.setName(updatedClassEntity.getName());
            //existingClassEntity.setSubjects(updatedClassEntity.getSubjects());
            return classEntityRepository.save(existingClassEntity);
        } else {
            throw new RuntimeException("ClassEntity not found with id " + id);
        }
    }

    @Override
    public void deleteClassEntity(Long id) {
        classEntityRepository.deleteById(id);
    }

    @Override
    public ClassEntity getClassEntityById(Long id) {
        return classEntityRepository.findById(id).orElseThrow(() ->
                new RuntimeException("ClassEntity not found with id " + id));
    }

    @Override
    public List<ClassEntity> getAllClassEntities() {
        return classEntityRepository.findAll();
    }

    @Override
    public Page<ClassEntity> searchClassEntities(String name, Pageable pageable) {
        return classEntityRepository.findByNameContainingIgnoreCase(name, pageable);
    }

}

