import { ChangeDetectorRef, Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { MCQOption } from 'app/models/mcq-option.model';
import { Question } from 'app/models/question.model';
import { MCQOptionService } from 'app/services/mcqoption-service.service';
import { QuestionService } from 'app/services/question.service';

@Component({
  selector: 'app-generate-paper',
  templateUrl: './generate-paper.component.html',
  styleUrls: ['./generate-paper.component.scss']
})
export class GeneratePaperComponent implements OnInit {

  mcqQuestions: Question[] = [];
  shortQuestions: Question[] = [];
  longQuestions: Question[] = [];
  mcqOptions: { [key: number]: MCQOption[] } = {};
  subjectId: number;

  constructor(private route: ActivatedRoute,
    private questionService: QuestionService,
    private cdr: ChangeDetectorRef,
    private mcqsService: MCQOptionService
  ) { }

  ngOnInit(): void {
    const id = this.route.snapshot.paramMap.get('id');
    this.getQuestionsBySubjectIdAndAddedToPaper(parseInt(id));
    this.subjectId = parseInt(id);
  }

  generatePaper() {
    this.questionService.generatePaper(this.subjectId).subscribe(
      () => {

      },
      error => {
        console.error('Error fetching questions:', error);
      }
    );
  }

  getQuestionsBySubjectIdAndAddedToPaper(id: number) {
    this.questionService.getQuestionsBySubjectIdAndAddedToPaper(id).subscribe(
      data => {
        console.log(data);

        this.filterQuestionsBySectionType(data);
      },
      error => {
        console.error('Error fetching questions:', error);
      }
    );
  }

  filterQuestionsBySectionType(questions: Question[]) {
    let questionId = []
    this.mcqQuestions = questions.filter(q => q.sectionType === 'MCQ');
    for (let i = 0; i < this.mcqQuestions.length; i++) {
      questionId.push(this.mcqQuestions[i].id);
    }
    if (questionId.length > 0) {
      this.getOptionsByMultipleQuestionIds(questionId)
    }
    this.shortQuestions = questions.filter(q => q.sectionType === 'SHORT_QUESTION');
    this.longQuestions = questions.filter(q => q.sectionType === 'LONG_QUESTION');

  }

  getOptionsByMultipleQuestionIds(questionIds: any[]) {
    this.mcqsService.getOptionsByMultipleQuestionIds(questionIds).subscribe(
      data => {
        console.log('MCQ options get successfully by question id:', data);
        this.mcqOptions = data;
        console.log(this.mcqOptions);

      },
      error => {
        console.error('Error creating MCQ options:', error);
      }
    )
  }

}
