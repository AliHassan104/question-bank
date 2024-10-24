package com.example.questionbank.controller;

import com.example.questionbank.model.MCQOption;
import com.example.questionbank.model.Question;
import com.example.questionbank.model.Subject;
import com.example.questionbank.model.enums.SectionType;
import com.example.questionbank.service.MCQOptionService;
import com.example.questionbank.service.QuestionService;
import com.example.questionbank.service.SubjectService;
import com.example.questionbank.service.impl.PdfService;
import com.example.questionbank.service.impl.WordService;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.thymeleaf.context.Context;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/questions")
public class QuestionController {

    @Autowired
    private QuestionService questionService;

    @Autowired
    private MCQOptionService mcqOptionService;

    @Autowired
    private SubjectService subjectService;

    @Autowired
    private PdfService pdfService;

    @Autowired
    private WordService wordService;

    @PostMapping
    public ResponseEntity<Question> createQuestion(@RequestBody Question question) {
        Question createdQuestion = questionService.createQuestion(question);
        return new ResponseEntity<>(createdQuestion, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Question> updateQuestion(@PathVariable Long id, @RequestBody Question question) {
        Question updatedQuestion = questionService.updateQuestion(id, question);
        return ResponseEntity.ok(updatedQuestion);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteQuestion(@PathVariable Long id) {
        questionService.deleteQuestion(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Question> getQuestionById(@PathVariable Long id) {
        Question question = questionService.getQuestionById(id);
        return ResponseEntity.ok(question);
    }

    @GetMapping
    public ResponseEntity<List<Question>> getAllQuestions() {
        List<Question> questions = questionService.getAllQuestions();
        return ResponseEntity.ok(questions);
    }

    @GetMapping("/pagination")
    public ResponseEntity<Page<Question>> getAllQuestionsPagination(Pageable pageable) {
        Page<Question> questions = questionService.getAllQuestionsPagination(pageable);
        return ResponseEntity.ok(questions);
    }

    @GetMapping("/search")
    public ResponseEntity<Page<Question>> searchQuestions(@RequestParam String questionText, Pageable pageable) {
        Page<Question> questions = questionService.searchQuestions(questionText, pageable);
        return ResponseEntity.ok(questions);
    }

    // GET /api/questions/filter?sectionType=MCQ&classId=1&subjectId=2&page=0&size=10
    @GetMapping("/filter")
    public ResponseEntity<Page<Question>> getFilteredQuestions(
            @RequestParam(required = false) SectionType sectionType,
            @RequestParam(required = false) Long chapterId,
            @RequestParam(required = false) Long subjectId,
            @RequestParam(required = false) Long classId,
            Pageable pageable) {

        Page<Question> filteredQuestions = questionService.getFilteredQuestions(sectionType, chapterId, subjectId, classId, pageable);
        return ResponseEntity.ok(filteredQuestions);
    }

    @PatchMapping("/{id}/toggle-paper-status")
    public ResponseEntity<Question> toggleAddedToPaper(@PathVariable Long id) {
        try {
            Question updatedQuestion = questionService.toggleAddedToPaper(id);
            return ResponseEntity.ok(updatedQuestion);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
    // 1. Get all questions based on subject ID
    @GetMapping("/subject/{subjectId}")
    public ResponseEntity<List<Question>> getQuestionsBySubjectId(@PathVariable Long subjectId) {
        List<Question> questions = questionService.getQuestionsBySubjectId(subjectId);
        return ResponseEntity.ok(questions);
    }

    // 2. Get questions based on subject ID and added to paper status
    @GetMapping("/subject/{subjectId}/added-to-paper")
    public ResponseEntity<List<Question>> getQuestionsBySubjectIdAndAddedToPaper(
            @PathVariable Long subjectId) {
        List<Question> questions = questionService.getQuestionsBySubjectIdAndAddedToPaper(subjectId);
        return ResponseEntity.ok(questions);
    }

    @GetMapping("/export/word")
    public void exportToWord(HttpServletResponse response) throws Exception {
        response.setContentType("application/vnd.openxmlformats-officedocument.wordprocessingml.document");
        response.setHeader("Content-Disposition", "attachment; filename=questions.docx");

        XWPFDocument document = new XWPFDocument();

        // Add content to Word
        XWPFParagraph sectionA = document.createParagraph();
        sectionA.createRun().setText("Section A: MCQs");
        // Add each MCQ question here

        XWPFParagraph sectionB = document.createParagraph();
        sectionB.createRun().setText("Section B: Short Answer Questions");
        // Add short answer questions here

        XWPFParagraph sectionC = document.createParagraph();
        sectionC.createRun().setText("Section C: Long Answer Questions");
        // Add long answer questions here

        document.write(response.getOutputStream());
        document.close();
    }

    @GetMapping("/question-bank/pdf/{subjectId}")
    @ResponseBody
    public void downloadPdf(HttpServletResponse response,
                            @PathVariable Long subjectId,
                            @RequestParam(value = "paperName", defaultValue = "0", required = false) Integer paperName,
                            @RequestParam(value = "paperDate", defaultValue = "0", required = false) Integer paperDate,
                            @RequestParam(value = "time", defaultValue = "0", required = false) Integer time,
                            @RequestParam(value = "marksSectionA", defaultValue = "0", required = false) Integer marksSectionA,
                            @RequestParam(value = "marksSectionB", defaultValue = "0", required = false) Integer marksSectionB,
                            @RequestParam(value = "marksSectionC", defaultValue = "0", required = false) Integer marksSectionC
    ) throws IOException {
        Context context = new Context();

        Subject subject = subjectService.getSubjectById(subjectId);

        context.setVariable("className"     , subject.getClassEntity().getName());
        context.setVariable("subjectName"   , subject.getName());

        context.setVariable("paperName"     , paperName);
        context.setVariable("paperDate"     , paperDate);
        context.setVariable("time"          , time);
        context.setVariable("marksSectionA" , marksSectionA);
        context.setVariable("marksSectionB" , marksSectionB);
        context.setVariable("marksSectionC" , marksSectionC);

        List<Question> questions = questionService.getQuestionsBySubjectId(subjectId);

        List<Question> mcqQuestions = questions.stream()
                .filter(q -> q.getSectionType() == SectionType.MCQ)
                .collect(Collectors.toList());

        if (!mcqQuestions.isEmpty()) {
            List<Long> questionIds = new ArrayList<>();
            mcqQuestions.forEach(e -> questionIds.add(e.getId()));

            Map<Long, List<MCQOption>> options = mcqOptionService.getOptionsByMultipleQuestionIds(questionIds);
            context.setVariable("mcqOptionsMap", options);
        }


        List<Question> shortQuestions = questions.stream()
                .filter(q -> q.getSectionType() == SectionType.SHORT_QUESTION)
                .collect(Collectors.toList());

        List<Question> longQuestions = questions.stream()
                .filter(q -> q.getSectionType() == SectionType.LONG_QUESTION)
                .collect(Collectors.toList());

        context.setVariable("MCQ", mcqQuestions);
        context.setVariable("shortQuestions", shortQuestions);
        context.setVariable("longQuestions", longQuestions);

        byte[] pdfContents = pdfService.generatePdf(context);

        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition", "attachment; filename=question-bank.pdf");
        response.getOutputStream().write(pdfContents);
    }

    @GetMapping("/question-bank/word")
    @ResponseBody
    public void downloadWord(HttpServletResponse response) throws IOException {
        Context context = new Context();
        context.setVariable("className", "Class 10");
        context.setVariable("subjectName", "Mathematics");

        context.setVariable("mcqs", null);
        context.setVariable("shortQuestions", null);
        context.setVariable("longQuestions", null);

        byte[] wordContents = null;
        try {
            wordContents = wordService.generateWord(context);
        } catch (Exception e) {
            e.printStackTrace();
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error generating Word document.");
            return; // Exit the method to prevent further processing
        }

        if (wordContents == null) {
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Word contents are empty.");
            return; // Exit if contents are null
        }

        response.setContentType("application/vnd.openxmlformats-officedocument.wordprocessingml.document");
        response.setHeader("Content-Disposition", "attachment; filename=question-bank.docx");
        response.getOutputStream().write(wordContents);
        response.getOutputStream().flush(); // Ensure all data is written to the output stream
    }


}

