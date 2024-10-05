import { Component, OnInit } from '@angular/core';
import { Chapter } from 'app/models/chapter.model';
import { ChapterService } from 'app/services/chapter.service';

@Component({
  selector: 'app-view-chapter',
  templateUrl: './view-chapter.component.html',
  styleUrls: ['./view-chapter.component.scss']
})
export class ViewChapterComponent implements OnInit {

  chapters: Chapter[] = [];
  selectedChapter: Chapter | null = null; // Track the selected chapter for editing

  constructor(private chapterService: ChapterService) { }

  ngOnInit(): void {
    this.getAllChapters(0, 10);
  }

  editChapter(chapter: Chapter): void {
    this.selectedChapter = chapter; // Pass the chapter to the form for editing
  }

  deleteChapter(chapter: Chapter): void {
    this.chapterService.deleteChapter(chapter.id).subscribe({
      next: () => {
        console.log('Chapter deleted successfully');
        this.getAllChapters(0, 10); // Refresh the chapters list
      },
      error: (error) => {
        console.error('Error deleting chapter:', error);
      }
    });
  }

  getAllChapters(page: number, size: number): void {
    this.chapterService.getAllChapters(page, size).subscribe({
      next: (data) => {
        this.chapters = data.content;
      },
      error: (error) => {
        console.error('Error fetching chapters:', error);
      }
    });
  }

  onChapterAdded(): void {
    this.getAllChapters(0, 10); // Refresh the list when a chapter is added or edited
    this.selectedChapter = null; // Reset the selected chapter after editing
  }
}
