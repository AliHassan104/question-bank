import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment'; // For base API URL
import { Chapter } from 'app/models/chapter.model';
import { Page } from 'app/models/page.model';

@Injectable({
  providedIn: 'root'
})
export class ChapterService {

  private apiUrl = `${environment.apiUrl}/api/chapters`;

  constructor(private http: HttpClient) { }

  // Create a new Chapter
  createChapter(chapter: Chapter): Observable<Chapter> {
    return this.http.post<Chapter>(this.apiUrl, chapter);
  }

  // Update a Chapter by ID
  updateChapter(id: number, chapter: Chapter): Observable<Chapter> {
    const url = `${this.apiUrl}/${id}`;
    return this.http.put<Chapter>(url, chapter);
  }

  // Delete a Chapter by ID
  deleteChapter(id: number): Observable<void> {
    const url = `${this.apiUrl}/${id}`;
    return this.http.delete<void>(url);
  }

  // Get a Chapter by ID
  getChapterById(id: number): Observable<Chapter> {
    const url = `${this.apiUrl}/${id}`;
    return this.http.get<Chapter>(url);
  }

  // Get all Chapters with pagination
  getAllChapters(): Observable<Chapter[]> {
    return this.http.get<Chapter[]>(this.apiUrl);
  }

  // Get all Chapters with pagination
  getAllActiveChapters(): Observable<Chapter[]> {
    const url = `${this.apiUrl}/active`;
    return this.http.get<Chapter[]>(url);
  }

  // Search Chapters by name with pagination
  searchChapters(name: string, page: number, size: number): Observable<Page<Chapter>> {
    const params = new HttpParams()
      .set('name', name)
      .set('page', page.toString())
      .set('size', size.toString());
    const url = `${this.apiUrl}/search`;
    return this.http.get<Page<Chapter>>(url, { params });
  }

  // Filter Chapters by subject and class with pagination
  filterChapters(subjectId?: number, classId?: number, paperLanguage?: string, mcqOptionSize?: string): Observable<Chapter[]> {
    let params = new HttpParams()

    if (subjectId !== undefined) {
      params = params.set('subjectId', subjectId.toString());
    }

    if (classId !== undefined) {
      params = params.set('classId', classId.toString());
    }

    if (paperLanguage !== undefined) {
      params = params.set('paperLanguage', paperLanguage);
    }

    if (mcqOptionSize !== undefined) {
      params = params.set('mcqOptionSize', mcqOptionSize);
    }

    const url = `${this.apiUrl}/filter`;
    return this.http.get<Chapter[]>(url, { params });
  }

  // Generate paper with template and sub-report options
  generatePaper(chapterId: number, options: {
    outputFormat: 'pdf' | 'word';
    wordTemplate?: string;
    pdfTemplate?: string;
    useSubReports?: boolean;
    subReports?: string[];
  }): Observable<Blob> {
    const url = `${this.apiUrl}/${chapterId}/generate`;
    return this.http.post(url, options, { responseType: 'blob' });
  }
}