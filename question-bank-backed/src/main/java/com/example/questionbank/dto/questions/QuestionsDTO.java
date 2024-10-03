package com.example.questionbank.dto.questions;

import com.example.questionbank.model.Subject;

import java.util.List;

public class QuestionsDTO {

    private Subject subject;

    private List<MCQsQuestions> mcQsQuestions;

    private List<ShortQuestions> shortQuestions;

    private List<LongQuestions> longQuestions;

}
