import { Component, OnInit } from '@angular/core';

@Component({
  selector: 'app-view-chapter',
  templateUrl: './view-chapter.component.html',
  styleUrls: ['./view-chapter.component.scss']
})
export class ViewChapterComponent implements OnInit {

  constructor() { }

  ngOnInit(): void {
  }

  chapters = [
    { name: 'Dakota Rice', country: 'Niger', city: 'Oud-Turnhout', salary: 36738 },
    { name: 'Minerva Hooper', country: 'Curaçao', city: 'Sinaai-Waas', salary: 23789 },
    { name: 'Sage Rodriguez', country: 'Netherlands', city: 'Baileux', salary: 56142 },
    { name: 'Philip Chaney', country: 'Korea, South', city: 'Overland Park', salary: 38735 }
    // Add more chapters as needed
  ];

  editChapter(chapter: any) {
    console.log('Editing chapter:', chapter);
    // Implement your logic for editing the chapter
  }

  deleteChapter(chapter: any) {
    console.log('Deleting chapter:', chapter);
    // Implement your logic for deleting the chapter
  }

}
