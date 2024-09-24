package com.example.questionbank.controller;

import com.example.questionbank.model.MCQOption;
import com.example.questionbank.service.MCQOptionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/mcq-options")
public class MCQOptionController {

    @Autowired
    private MCQOptionService mcqOptionService;

    @PostMapping
    public ResponseEntity<MCQOption> createMCQOption(@RequestBody MCQOption mcqOption) {
        MCQOption createdMCQOption = mcqOptionService.createMCQOption(mcqOption);
        return new ResponseEntity<>(createdMCQOption, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<MCQOption> updateMCQOption(@PathVariable Long id, @RequestBody MCQOption mcqOption) {
        MCQOption updatedMCQOption = mcqOptionService.updateMCQOption(id, mcqOption);
        return ResponseEntity.ok(updatedMCQOption);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMCQOption(@PathVariable Long id) {
        mcqOptionService.deleteMCQOption(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<MCQOption> getMCQOptionById(@PathVariable Long id) {
        MCQOption mcqOption = mcqOptionService.getMCQOptionById(id);
        return ResponseEntity.ok(mcqOption);
    }

    @GetMapping
    public ResponseEntity<Page<MCQOption>> getAllMCQOptions(Pageable pageable) {
        Page<MCQOption> mcqOptions = mcqOptionService.getAllMCQOptions(pageable);
        return ResponseEntity.ok(mcqOptions);
    }

    @GetMapping("/search")
    public ResponseEntity<Page<MCQOption>> searchMCQOptions(@RequestParam String optionText, Pageable pageable) {
        Page<MCQOption> mcqOptions = mcqOptionService.searchMCQOptions(optionText, pageable);
        return ResponseEntity.ok(mcqOptions);
    }
}

