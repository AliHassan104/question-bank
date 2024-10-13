package com.example.questionbank.service.impl;

import com.example.questionbank.model.Subject;
import com.example.questionbank.repository.SubjectRepository;
import com.example.questionbank.service.SubjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class SubjectServiceImplementation implements SubjectService {

    @Autowired
    private SubjectRepository subjectRepository;

    @Override
    public Subject createSubject(Subject subject) {
        return subjectRepository.save(subject);
    }

    @Override
    public Subject updateSubject(Long id, Subject updatedSubject) {
        Optional<Subject> existingSubjectOpt = subjectRepository.findById(id);
        if (existingSubjectOpt.isPresent()) {
            Subject existingSubject = existingSubjectOpt.get();
            existingSubject.setName(updatedSubject.getName());
            existingSubject.setClassEntity(updatedSubject.getClassEntity());
            //existingSubject.setChapters(updatedSubject.getChapters());
            return subjectRepository.save(existingSubject);
        } else {
            throw new RuntimeException("Subject not found with id " + id);
        }
    }

    @Override
    public void deleteSubject(Long id) {
        subjectRepository.deleteById(id);
    }

    @Override
    public Subject getSubjectById(Long id) {
        return subjectRepository.findById(id).orElseThrow(() ->
                new RuntimeException("Subject not found with id " + id));
    }

    @Override
    public List<Subject> getAllSubjects() {
        return subjectRepository.findAll();
    }

    @Override
    public List<Subject> searchSubjects(String name) {
        return subjectRepository.findByNameContainingIgnoreCase(name);
    }

    public List<Subject> filterSubjectsByClass(Long classId) {
        return subjectRepository.findByClassEntityId(classId);
    }

    @Override
    public Page<Subject> getAllSubjectsWithPagination(Pageable pageable) {
        return subjectRepository.findAll(pageable);
    }
}
