import { Component, OnInit } from '@angular/core';
import { Question } from 'app/models/question.model';
import { QuestionService } from 'app/services/question.service';

@Component({
  selector: 'app-view-question',
  templateUrl: './view-question.component.html',
  styleUrls: ['./view-question.component.scss']
})
export class ViewQuestionComponent implements OnInit {

  constructor(private questionService : QuestionService) { }

  ngOnInit(): void {
    this.getAllQuestions(0,10)
  }

  questions : Question[] = [];

  editQuestion(question: any) {
    console.log('Editing question:', question);
  }

  deleteQuestion(question: any) {
    console.log('Deleting question:', question);
  }

  getAllQuestions(page: number, size: number) {
    this.questionService.getAllQuestions(page, size).subscribe({
      next: data => {
        console.log(data.content);
        
        this.questions = data.content;
      },
      error: error => {
        console.error('Error fetching questions:', error);
      },
      complete: () => {
        console.log('Questions fetched successfully');
      }
    });
  }

  addQuestionToPaper(question: Question) {
    this.questionService.toggleAddedToPaper(question.id).subscribe({
      next: data => {
        console.log(data);

        this.getAllQuestions(0,10)
      },
      error: error => {
        console.error('Error fetching questions:', error);
      },
      complete: () => {
        console.log('Questions fetched successfully');
      }
    });
  }
  

}
