import { Component, OnInit } from '@angular/core';
import { Chapter } from 'app/models/chapter.model';
import { ChapterService } from 'app/services/chapter.service';

@Component({
  selector: 'app-view-chapter',
  templateUrl: './view-chapter.component.html',
  styleUrls: ['./view-chapter.component.scss']
})
export class ViewChapterComponent implements OnInit {

  chapters : Chapter[] = [];

  constructor(private chapterService : ChapterService) { }

  ngOnInit(): void {
    this.getAllChapters(0,10)
  }

  editChapter(chapter: any) {
    console.log('Editing chapter:', chapter);
  }

  deleteChapter(chapter: any) {
    console.log('Deleting chapter:', chapter);
  }

  getAllChapters(page: number, size: number) {
    this.chapterService.getAllChapters(page, size).subscribe({
      next: (data) => {
        this.chapters = data.content;
      },
      error: (error) => {
        console.error('Error fetching chapters:', error);
      },
      complete: () => {
        // Optional: Handle any logic after the observable completes.
        console.log('Fetch completed');
      }
    });
  }
  


}
