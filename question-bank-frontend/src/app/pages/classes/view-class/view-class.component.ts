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
    this.getAllClasses();
  }

  // Event listener to update the list of classes
  onClassUpdated() {
    this.getAllClasses(); // Refresh class list
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
        this.getAllClasses(); // Refresh after deletion
      },
      error => {
        console.error('Error deleting class:', error);
      }
    );
  }

  getAllClasses() {
    this.classEntityService.getAllClassEntities().subscribe(
      data => {
        console.log(data);

        this.classes = data;
      },
      error => {
        console.error('Error fetching classes:', error);
      }
    );
  }
}
