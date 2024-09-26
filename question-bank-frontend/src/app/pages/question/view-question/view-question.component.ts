import { Component, OnInit } from '@angular/core';

@Component({
  selector: 'app-view-question',
  templateUrl: './view-question.component.html',
  styleUrls: ['./view-question.component.scss']
})
export class ViewQuestionComponent implements OnInit {

  constructor() { }

  ngOnInit(): void {
  }

  questions = [
    { text: 'What is IDE?', class: 'X', section: 'MCQs', chapter: 'Chapter 1' },
    { text: 'Define OOP.', class: 'XII', section: 'Short', chapter: 'Chapter 2' },
    { text: 'Explain recursion.', class: 'XI', section: 'Long', chapter: 'Chapter 3' }
    // Add more questions as needed
  ];

  editQuestion(question: any) {
    console.log('Editing question:', question);
    // Implement your logic for editing the question
  }

  deleteQuestion(question: any) {
    console.log('Deleting question:', question);
    // Implement your logic for deleting the question
  }

}
