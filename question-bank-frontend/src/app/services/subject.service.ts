import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Subject, CreateSubjectRequest, UpdateSubjectRequest } from '../models/subject.model';
import { Page } from '../models/page.model';
import { environment } from '../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class SubjectService {

  private apiUrl = `${environment.apiUrl}/api/subjects`;

  constructor(private http: HttpClient) { }

  // Create a new Subject - takes CreateSubjectRequest, returns Subject
  createSubject(subject: CreateSubjectRequest): Observable<Subject> {
    return this.http.post<Subject>(this.apiUrl, subject);
  }

  // Update a Subject by ID - takes UpdateSubjectRequest, returns Subject
  updateSubject(id: number, subject: UpdateSubjectRequest): Observable<Subject> {
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

  // Get all Subjects
  
  getAllSubjects(): Observable<Subject[]> {
    return this.http.get<Subject[]>(this.apiUrl);
  }
  // Get all active Subjects
  
  getAllActiveSubjects(): Observable<Subject[]> {
    const url = `${this.apiUrl}/active`;
    return this.http.get<Subject[]>(url);
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

  // filterSubjectsByClass(classId: number): Observable<Subject[]> {
  //   const params = new HttpParams().set('classId', classId.toString());
  //   const url = `${this.apiUrl}/class`;
  //   return this.http.get<Subject[]>(url, { params });
  // }

  // Get subjects by class ID - CORRECTED URL
  getSubjectsByClass(classId: number): Observable<Subject[]> {
    const url = `${this.apiUrl}/class/${classId}`; // Using path variable
    return this.http.get<Subject[]>(url).pipe(
    );
  }

  // Alternative method using query parameter
  // getSubjectsByClassQuery(classId: number): Observable<Subject[]> {
  //   const url = `${this.apiUrl}/class?classId=${classId}`; // Using query parameter
  //   return this.http.get<Subject[]>(url).pipe(
  //   );
  // }
}