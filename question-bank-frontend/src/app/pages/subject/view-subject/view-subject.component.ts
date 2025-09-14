import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { ClassEntity } from 'app/models/class-entities.model';
import { Subject } from 'app/models/subject.model';
import { ClassEntityService } from 'app/services/class-entity.service';
import { SubjectService } from 'app/services/subject.service';
import * as e from 'express';

@Component({
  selector: 'app-view-subject',
  templateUrl: './view-subject.component.html',
  styleUrls: ['./view-subject.component.scss']
})
export class ViewSubjectComponent implements OnInit {

  subjects: Subject[] = [];
  editingSubject: Subject | null = null;
  classes: ClassEntity[] = [];

  constructor(private subjectService: SubjectService, private route: ActivatedRoute, private classEntityService: ClassEntityService) { }

  ngOnInit(): void {
    this.getAllSubjects();
    this.getAllClasses()
  }

  onSubjectUpdated() {
    this.getAllSubjects(); // Refresh subject list
    this.editingSubject = null; // Reset editing mode
  }

  // Reset editing mode
  resetEditingMode() {
    this.editingSubject = null;
  }

  editSubject(subject: Subject) {
    this.editingSubject = subject;
  }

  deleteSubject(subject: Subject) {
    this.subjectService.deleteSubject(subject.id).subscribe(
      () => {
        this.getAllSubjects(); // Refresh subject list after deletion
      },
      error => {
        console.error('Error deleting subject:', error);
      }
    );
  }

  getAllSubjects() {
    this.subjectService.getAllActiveSubjects().subscribe(
      data => {
        this.subjects = data;
        
      },
      error => {
        console.error('Error fetching subjects:', error);
      }
    );
  }

  getAllClasses() {
    this.classEntityService.getAllActiveClasses().subscribe(
      data => {
        this.classes = data;
      },
      error => {
        console.error('Error fetching classes:', error);
      }
    );
  }

  getFilteredSubject(classId: number) {
    this.subjectService.getSubjectsByClass(classId).subscribe(
      data => {
        this.subjects = data;
      },
      error => {
        console.error('Error fetching classes:', error);
      }
    );
  }

  onClassChange(value: string) {
    if (value == 'all' || value == '') {
      this.getAllSubjects();
    } else {
      this.getFilteredSubject(parseInt(value))
    }
  }

}
