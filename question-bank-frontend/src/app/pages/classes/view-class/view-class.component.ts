import { Component, OnInit } from '@angular/core';
import { ClassEntity } from 'app/models/class-entities.model';
import { ClassEntityService } from 'app/services/class-entity.service';

@Component({
  selector: 'app-view-class',
  templateUrl: './view-class.component.html',
  styleUrls: ['./view-class.component.scss']
})
export class ViewClassComponent implements OnInit {

  constructor(private classEntityService : ClassEntityService) { }

  ngOnInit(): void {
    const defaultPage = 0;
    const defaultSize = 10;
    this.getAllClasses(defaultPage, defaultSize);
  }
  
  editClass(classItem: any) {
    console.log('Editing class:', classItem);
  }

  deleteClass(classItem: any) {
    console.log('Deleting class:', classItem);
  }

  classes: ClassEntity[] = [];

  totalPages: number = 0;

getAllClasses(page: number, size: number) {
  debugger;
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
