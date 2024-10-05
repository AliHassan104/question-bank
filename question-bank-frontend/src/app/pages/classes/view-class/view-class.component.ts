import { Component, OnInit } from '@angular/core';
import { ClassEntity } from 'app/models/class-entities.model';
import { ClassEntityService } from 'app/services/class-entity.service';

@Component({
  selector: 'app-view-class',
  templateUrl: './view-class.component.html',
  styleUrls: ['./view-class.component.scss']
})
export class ViewClassComponent implements OnInit {

  classes: ClassEntity[] = [];
  editingClass: ClassEntity | null = null;

  constructor(private classEntityService: ClassEntityService) { }

  ngOnInit(): void {
    const defaultPage = 0;
    const defaultSize = 10;
    this.getAllClasses(defaultPage, defaultSize);
  }

  // Event listener to update the list of classes
  onClassUpdated() {
    this.getAllClasses(0, 10); // Refresh class list
    this.editingClass = null; // Reset editing mode to null after update
  }

  // Reset editing mode when an update is complete
  resetEditingMode() {
    this.editingClass = null;
  }

  editClass(classItem: ClassEntity) {
    this.editingClass = classItem; // Set the class to be edited
  }

  deleteClass(classItem: ClassEntity) {
    this.classEntityService.deleteClassEntity(classItem.id).subscribe(
      () => {
        this.getAllClasses(0, 10); // Refresh after deletion
      },
      error => {
        console.error('Error deleting class:', error);
      }
    );
  }

  getAllClasses(page: number, size: number) {
    this.classEntityService.getAllClassEntities(page, size).subscribe(
      data => {
        this.classes = data.content;
      },
      error => {
        console.error('Error fetching classes:', error);
      }
    );
  }
}
