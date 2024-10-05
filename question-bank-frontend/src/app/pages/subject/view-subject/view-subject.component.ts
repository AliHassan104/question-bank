import { Component, OnInit } from '@angular/core';
import { Subject } from 'app/models/subject.model';
import { SubjectService } from 'app/services/subject.service';

@Component({
  selector: 'app-view-subject',
  templateUrl: './view-subject.component.html',
  styleUrls: ['./view-subject.component.scss']
})
export class ViewSubjectComponent implements OnInit {

  subjects: Subject[] = [];
  editingSubject: Subject | null = null;
  //showAddSubject: boolean = true;  // Control whether to show add subject form

  constructor(private subjectService: SubjectService) { }

  ngOnInit(): void {
    const defaultPage = 0;
    const defaultSize = 10;
    this.getAllSubjects(defaultPage, defaultSize);
  }

  // Event listener to update the subject list
  onSubjectUpdated() {
    this.getAllSubjects(0, 10); // Refresh subject list
    this.editingSubject = null; // Reset editing mode
  }

  // Reset editing mode
  resetEditingMode() {
    this.editingSubject = null;
  }

  editSubject(subject: Subject) {
    this.editingSubject = subject; // Set the subject to be edited
    //this.showAddSubject = true; // Show the add subject form
  }

  deleteSubject(subject: Subject) {
    this.subjectService.deleteSubject(subject.id).subscribe(
      () => {
        this.getAllSubjects(0, 10); // Refresh subject list after deletion
      },
      error => {
        console.error('Error deleting subject:', error);
      }
    );
  }

  getAllSubjects(page: number, size: number) {
    this.subjectService.getAllSubjects(page, size).subscribe(
      data => {
        this.subjects = data.content;
      },
      error => {
        console.error('Error fetching subjects:', error);
      }
    );
  }
}
