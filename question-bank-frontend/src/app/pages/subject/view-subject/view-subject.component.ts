import { Component, OnInit } from '@angular/core';
import { Subject } from 'app/models/subject.model';
import { SubjectService } from 'app/services/subject.service';

@Component({
  selector: 'app-view-subject',
  templateUrl: './view-subject.component.html',
  styleUrls: ['./view-subject.component.scss']
})
export class ViewSubjectComponent implements OnInit {

  constructor(private subjectService : SubjectService) { }

  ngOnInit(): void {
    const defaultPage = 0;
    const defaultSize = 10;
    this.getAllSubjects(defaultPage,defaultSize);
  }

  editSubject(subject: any) {
    console.log('Editing subject:', subject);
  }

  deleteSubject(subject: any) {
    console.log('Deleting subject:', subject);
  }

  subjects: Subject[] = [];

  getAllSubjects(page: number, size: number) {
    this.subjectService.getAllSubjects(page, size).subscribe(
      data => {
        console.log(data.content);        
        this.subjects = data.content;
      },
      error => {
        console.error('Error fetching classes:', error);
      }
    );
  }

  get(page: number, size: number) {
    this.subjectService.getAllSubjects(page, size).subscribe(
      data => {
        console.log(data.content);        
        this.subjects = data.content;
      },
      error => {
        console.error('Error fetching classes:', error);
      }
    );
  }

}
