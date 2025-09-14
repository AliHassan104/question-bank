import { Injectable } from '@angular/core';
import { HttpClient, HttpParams, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';
import { tap, catchError } from 'rxjs/operators';
import { environment } from '../../environments/environment';
import { ClassEntity } from 'app/models/class-entities.model';
import { Page } from 'app/models/page.model';

@Injectable({
  providedIn: 'root'
})
export class ClassEntityService {

  private apiUrl = `${environment.apiUrl}/api/classes`;

  constructor(private http: HttpClient) { }

  // Helper method to get auth headers
  private getAuthHeaders(): HttpHeaders {
    const token = localStorage.getItem('token');
    
    if (token) {
      return new HttpHeaders({
        'Authorization': `Bearer ${token}`,
        'Content-Type': 'application/json'
      });
    }
    
    return new HttpHeaders({
      'Content-Type': 'application/json'
    });
  }

  // Debug method to test API connectivity
  // testConnection(): Observable<any> {
  //   console.log('Testing connection to:', this.apiUrl);
  //   return this.http.get(`${environment.apiUrl}/actuator/health`, { headers: this.getAuthHeaders() })
  //     .pipe(
  //       tap(response => console.log('Health check response:', response)),
  //       catchError(error => {
  //         console.error('Health check failed:', error);
  //         throw error;
  //       })
  //     );
  // }

  // Create a new ClassEntity
  createClassEntity(classEntity: ClassEntity): Observable<ClassEntity> {

    
    return this.http.post<ClassEntity>(this.apiUrl, classEntity, { headers: this.getAuthHeaders() })
      .pipe(
        tap(response => {
        }),
        catchError(error => {
          console.error('Error creating class:', error);
          console.error('Error status:', error.status);
          console.error('Error message:', error.message);
          console.error('Error body:', error.error);
          throw error;
        })
      );
  }

  // Update a ClassEntity by ID
  updateClassEntity(id: number, classEntity: ClassEntity): Observable<ClassEntity> {
    const url = `${this.apiUrl}/${id}`;
    
    return this.http.put<ClassEntity>(url, classEntity, { headers: this.getAuthHeaders() })
      .pipe(
        tap(response => {
          console.log('Class updated successfully:', response);
        }),
        catchError(error => {
          console.error('Error updating class:', error);
          throw error;
        })
      );
  }

  // Delete a ClassEntity by ID
  deleteClassEntity(id: number): Observable<void> {
    const url = `${this.apiUrl}/${id}`;
    
    return this.http.delete<void>(url, { headers: this.getAuthHeaders() })
      .pipe(
        tap(() => {
          console.log('Class deleted successfully');
        }),
        catchError(error => {
          console.error('Error deleting class:', error);
          throw error;
        })
      );
  }

  // Get a ClassEntity by ID
  getClassEntityById(id: number): Observable<ClassEntity> {
    const url = `${this.apiUrl}/${id}`;
    
    return this.http.get<ClassEntity>(url, { headers: this.getAuthHeaders() })
      .pipe(
        tap(response => {
          console.log('Class fetched by ID:', response);
        }),
        catchError(error => {
          console.error('Error fetching class by ID:', error);
          throw error;
        })
      );
  }

  // Get all ClassEntities
  getAllClassEntities(): Observable<ClassEntity[]> {
    
    return this.http.get<ClassEntity[]>(this.apiUrl, { headers: this.getAuthHeaders() })
      .pipe(
        tap(response => {
          console.log('All classes fetched successfully:', response);
        }),
        catchError(error => {
          console.error('Error fetching all classes:', error);
          console.error('Request URL:', this.apiUrl);
          console.error('Request headers:', this.getAuthHeaders());
          console.error('Error details:', {
            status: error.status,
            statusText: error.statusText,
            url: error.url,
            message: error.message,
            error: error.error
          });
          throw error;
        })
      );
  }

  // Get all with pagination
  getAllClassEntitiesWithPagination(page: number, size: number): Observable<Page<ClassEntity>> {
    const params = new HttpParams()
      .set('page', page.toString())
      .set('size', size.toString());
    const url = `${this.apiUrl}/page`;
    
    console.log('Fetching classes with pagination:', { page, size });
    console.log('GET URL:', url);
    
    return this.http.get<Page<ClassEntity>>(url, { headers: this.getAuthHeaders(), params })
      .pipe(
        tap(response => {
          console.log('Paginated classes fetched:', response);
        }),
        catchError(error => {
          console.error('Error fetching paginated classes:', error);
          throw error;
        })
      );
  }

  // Get only active classes
  getAllActiveClasses(): Observable<ClassEntity[]> {
    const url = `${this.apiUrl}/active`;

    return this.http.get<ClassEntity[]>(url, { headers: this.getAuthHeaders() })
      .pipe(
        tap(response => {
        }),
        catchError(error => {
          console.error('Error fetching active classes:', error);
          throw error;
        })
      );
  }

  // Search ClassEntities by name with pagination
  searchClassEntities(name: string, page: number, size: number): Observable<Page<ClassEntity>> {
    const params = new HttpParams()
      .set('name', name)
      .set('page', page.toString())
      .set('size', size.toString());
    const url = `${this.apiUrl}/search`;
    
    return this.http.get<Page<ClassEntity>>(url, { headers: this.getAuthHeaders(), params })
      .pipe(
        tap(response => {
          console.log('Search results:', response);
        }),
        catchError(error => {
          console.error('Error searching classes:', error);
          throw error;
        })
      );
  }

  // Check if class name exists
  checkClassNameExists(name: string): Observable<boolean> {
    const params = new HttpParams().set('name', name);
    const url = `${this.apiUrl}/exists`;
    
    return this.http.get<boolean>(url, { headers: this.getAuthHeaders(), params })
      .pipe(
        tap(response => {
          console.log('Class name exists check:', response);
        }),
        catchError(error => {
          console.error('Error checking class name:', error);
          throw error;
        })
      );
  }
}