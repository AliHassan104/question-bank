import { ChangeDetectorRef, Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { Question } from 'app/models/question.model';
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

  constructor(private route: ActivatedRoute , private questionService: QuestionService, private cdr: ChangeDetectorRef) {}

  ngOnInit(): void {
    const id = this.route.snapshot.paramMap.get('id');
    this.getQuestionsBySubjectIdAndAddedToPaper(parseInt(id));
  }

  getQuestionsBySubjectIdAndAddedToPaper(id: number) {
    this.questionService.getQuestionsBySubjectIdAndAddedToPaper(id).subscribe(
      data => {
        this.filterQuestionsBySectionType(data);
      },
      error => {
        console.error('Error fetching questions:', error);
      }
    );
  }

  filterQuestionsBySectionType(questions: Question[]) {
    this.mcqQuestions = questions.filter(q => q.sectionType === 'MCQ');
    this.shortQuestions = questions.filter(q => q.sectionType === 'SHORT_QUESTION');
    this.longQuestions = questions.filter(q => q.sectionType === 'LONG_QUESTION');

    this.cdr.markForCheck();
  }
  
}
