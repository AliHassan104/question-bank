import { Component, OnInit, Output, EventEmitter, Input } from '@angular/core';
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

  @Output() chapterAdded = new EventEmitter<void>(); // Emit event when chapter is added or edited
  @Input() selectedChapter: Chapter | null = null;   // Input for editing

  constructor(private fb: FormBuilder, private classEntityService: ClassEntityService,
    private subjectService: SubjectService, private chapterService: ChapterService) { }

  ngOnInit(): void {
    this.getAllClasses();
    this.getAllSubjects();

    // Initialize form
    this.chapterForm = this.fb.group({
      name: ['', Validators.required],
      class: [''], //, Validators.required
      subject: ['', Validators.required],
    });

    // If we are editing, fill the form with the chapter's data
    if (this.selectedChapter) {
      this.chapterForm.patchValue({
        name: this.selectedChapter.name,
        class: this.selectedChapter.subject.classEntity.id,
        subject: this.selectedChapter.subject.id,
      });
    }
  }

  ngOnChanges(): void {
    // If the selected chapter changes, update the form fields
    if (this.selectedChapter) {
      this.chapterForm.patchValue({
        name: this.selectedChapter.name,
        class: this.selectedChapter.subject.classEntity.id,
        subject: this.selectedChapter.subject.id,
      });
    } else {
      this.chapterForm.reset(); // Reset the form if no chapter is selected
    }
  }

  onSubmit(): void {
    if (this.chapterForm.valid) {
      const newChapterEntity: Chapter = {
        name: this.chapterForm.value.name,
        subject: {
          id: this.chapterForm.value.subject,
          name: '',
          classEntity: null
        }
      };

      if (this.selectedChapter) {
        this.updateChapterEntity(this.selectedChapter.id, newChapterEntity);
      } else {
        this.createChapterEntity(newChapterEntity);
      }
    }
  }

  createChapterEntity(chapter: Chapter): void {
    this.chapterService.createChapter(chapter).subscribe({
      next: (data) => {
        console.log('Chapter created successfully:', data);
        this.chapterAdded.emit();
        this.chapterForm.reset();
      },
      error: (error) => {
        console.error('Error creating chapter:', error);
      }
    });
  }

  updateChapterEntity(id: number, chapter: Chapter): void {
    this.chapterService.updateChapter(id, chapter).subscribe({
      next: (data) => {
        console.log('Chapter updated successfully:', data);
        this.chapterAdded.emit(); // Emit event to update the chapter view
        this.chapterForm.reset(); // Reset the form
      },
      error: (error) => {
        console.error('Error updating chapter:', error);
      }
    });
  }

  getAllClasses(): void {
    this.classEntityService.getAllClassEntities().subscribe({
      next: (data) => {
        this.classes = data;
      },
      error: (error) => {
        console.error('Error fetching classes:', error);
      }
    });
  }

  getAllSubjects(): void {
    this.subjectService.getAllSubjects().subscribe({
      next: (data) => {
        this.subjects = data;
      },
      error: (error) => {
        console.error('Error fetching subjects:', error);
      }
    });
  }

  onClassChange(value: string) {
    if (value == 'all' || value == '') {
      this.getAllSubjects();
    } else {
      this.getFilteredSubject(parseInt(value))
    }
  }

  getFilteredSubject(classId: number) {
    this.subjectService.filterSubjectsByClass(classId).subscribe(
      data => {
        this.subjects = data;
      },
      error => {
        console.error('Error fetching classes:', error);
      }
    );
  }
}
