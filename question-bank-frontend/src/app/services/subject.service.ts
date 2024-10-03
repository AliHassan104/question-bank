import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Subject } from '../models/subject.model'; // Adjust the path based on your folder structure
import { Page } from '../models/page.model'; // For pagination model
import { environment } from '../../environments/environment'; // For base API URL

@Injectable({
  providedIn: 'root'
})
export class SubjectService {

  private apiUrl = `${environment.apiUrl}/api/subjects`;

  constructor(private http: HttpClient) {}

  // Create a new Subject
  createSubject(subject: Subject): Observable<Subject> {
    return this.http.post<Subject>(this.apiUrl, subject);
  }

  // Update a Subject by ID
  updateSubject(id: number, subject: Subject): Observable<Subject> {
    const url = `${this.apiUrl}/${id}`;
    return this.http.put<Subject>(url, subject);
  }

  // Delete a Subject by ID
  deleteSubject(id: number): Observable<void> {
    const url = `${this.apiUrl}/${id}`;
    return this.http.delete<void>(url);
  }

  // Get a Subject by ID
  getSubjectById(id: number): Observable<Subject> {
    const url = `${this.apiUrl}/${id}`;
    return this.http.get<Subject>(url);
  }

  // Get all Subjects with pagination
  getAllSubjects(page: number, size: number): Observable<Page<Subject>> {
    const params = new HttpParams()
      .set('page', page.toString())
      .set('size', size.toString());
    return this.http.get<Page<Subject>>(this.apiUrl);
    // return this.http.get<Page<Subject>>(this.apiUrl, { params });
  }

  // Search Subjects by name with pagination
  searchSubjects(name: string, page: number, size: number): Observable<Page<Subject>> {
    const params = new HttpParams()
      .set('name', name)
      .set('page', page.toString())
      .set('size', size.toString());
    const url = `${this.apiUrl}/search`;
    return this.http.get<Page<Subject>>(url, { params });
  }
}
