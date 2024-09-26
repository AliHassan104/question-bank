import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
// import { ClassEntity } from '../models/class-entity.model';
// import { Page } from '../models/page.model';
import { environment } from '../../environments/environment'; // Base API URL
import { ClassEntity } from 'app/models/class-entities.model';
import { Page } from 'app/models/page.model';

@Injectable({
  providedIn: 'root'
})
export class ClassEntityService {

  private apiUrl = `${environment.apiUrl}/api/class-entities`;

  constructor(private http: HttpClient) {}

  // Create a new ClassEntity
  createClassEntity(classEntity: ClassEntity): Observable<ClassEntity> {
    return this.http.post<ClassEntity>(this.apiUrl, classEntity);
  }

  // Update a ClassEntity by ID
  updateClassEntity(id: number, classEntity: ClassEntity): Observable<ClassEntity> {
    const url = `${this.apiUrl}/${id}`;
    return this.http.put<ClassEntity>(url, classEntity);
  }

  // Delete a ClassEntity by ID
  deleteClassEntity(id: number): Observable<void> {
    const url = `${this.apiUrl}/${id}`;
    return this.http.delete<void>(url);
  }

  // Get a ClassEntity by ID
  getClassEntityById(id: number): Observable<ClassEntity> {
    const url = `${this.apiUrl}/${id}`;
    return this.http.get<ClassEntity>(url);
  }

  // Get all ClassEntities with pagination
  getAllClassEntities(page: number, size: number): Observable<Page<ClassEntity>> {
    const params = new HttpParams()
      .set('page', page.toString())
      .set('size', size.toString());
    return this.http.get<Page<ClassEntity>>(this.apiUrl, { params });
  }

  // Search ClassEntities by name with pagination
  searchClassEntities(name: string, page: number, size: number): Observable<Page<ClassEntity>> {
    const params = new HttpParams()
      .set('name', name)
      .set('page', page.toString())
      .set('size', size.toString());
    const url = `${this.apiUrl}/search`;
    return this.http.get<Page<ClassEntity>>(url, { params });
  }
}
