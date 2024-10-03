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

  constructor(private http: HttpClient) {}

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
  getAllChapters(page: number, size: number): Observable<Page<Chapter>> {
    const params = new HttpParams()
      .set('page', page.toString())
      .set('size', size.toString());
    return this.http.get<Page<Chapter>>(this.apiUrl);
    // return this.http.get<Page<Chapter>>(this.apiUrl, { params });
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
}
