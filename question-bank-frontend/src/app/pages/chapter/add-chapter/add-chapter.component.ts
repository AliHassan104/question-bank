import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Chapter } from 'app/models/chapter.model';
import { ClassEntity } from 'app/models/class-entities.model';
import { Subject } from 'app/models/subject.model';
import { ChapterService } from 'app/services/chapter.service';
import { ClassEntityService } from 'app/services/class-entity.service';
import { SubjectService } from 'app/services/subject.service';

@Component({
  selector: 'app-add-chapter',
  templateUrl: './add-chapter.component.html',
  styleUrls: ['./add-chapter.component.scss']
})
export class AddChapterComponent implements OnInit {

  classes: ClassEntity[] = [];
  subjects: Subject[] = [];
  chapterForm: FormGroup;

  constructor(private fb: FormBuilder, private classEntityService : ClassEntityService,
    private subjectService : SubjectService,
    private chapterService : ChapterService
  ) {}

  ngOnInit(): void {
    
    this.getAllClasses(0,10)

    this.getAllSubjects(0,10)

    this.chapterForm = this.fb.group({
      name: ['', Validators.required],
      class: ['', Validators.required],
      subject: ['', Validators.required],
    });
  }

  onSubmit(): void {
    if (this.chapterForm.valid) {

      const newChapterEntity: Chapter = {
        name: this.chapterForm.value.name,
        subject : {
          id: this.chapterForm.value.subject,
          name: '',
          classEntity: null
        }
      };
      
      this.createChapterEntity(newChapterEntity);
      
      this.chapterForm.reset();
    }
  }

  getAllClasses(page: number, size: number) {
    this.classEntityService.getAllClassEntities(page, size).subscribe({
      next: (data) => {
        this.classes = data.content;
      },
      error: (error) => {
        console.error('Error fetching classes:', error);
      },
      complete: () => {
        // Optional: Handle any logic after the observable completes.
        console.log('Fetch completed');
      }
    });
  }
  
  getAllSubjects(page: number, size: number) {
    this.subjectService.getAllSubjects(page, size).subscribe({
      next: (data) => {
        this.subjects = data.content;
      },
      error: (error) => {
        console.error('Error fetching subjects:', error);
      },
      complete: () => {
        // Optional: Handle any logic after the observable completes.
        console.log('Fetch completed');
      }
    });
  }  

  createChapterEntity(chapter: Chapter) {
    this.chapterService.createChapter(chapter).subscribe({
      next: (data) => {
        console.log('Chapter created successfully:', data);
        // You can add additional checks if needed, for example:
        if (data?.id) {
          console.log(`Created chapter ID: ${data.id}`);
        } else {
          console.warn('Chapter creation successful, but no ID returned.');
        }
      },
      error: (error) => {
        console.error('Error creating chapter:', error);
      },
      complete: () => {
        console.log('Chapter creation request completed');
      }
    });
  }
  

}