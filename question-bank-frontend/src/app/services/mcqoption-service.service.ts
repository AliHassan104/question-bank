import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { MCQOption } from '../models/mcq-option.model'; // Adjust the path based on your folder structure
import { Page } from '../models/page.model'; // For pagination model
import { environment } from '../../environments/environment'; // For base API URL

@Injectable({
  providedIn: 'root'
})
export class MCQOptionService {

  private apiUrl = `${environment.apiUrl}/api/mcq-options`;

  constructor(private http: HttpClient) { }

  // Create a new MCQ Option
  createMCQOption(mcqOption: MCQOption): Observable<MCQOption> {
    return this.http.post<MCQOption>(this.apiUrl, mcqOption);
  }

  createMultipleMCQOptions(mcqOptions: MCQOption[]): Observable<MCQOption[]> {
    console.log(mcqOptions);
    const url = `${this.apiUrl}/multiple`; // Adjust based on your API endpoint
    console.log(url);
    return this.http.post<MCQOption[]>(url, mcqOptions);
  }

  // Update an MCQ Option by ID
  updateMCQOption(id: number, mcqOption: MCQOption): Observable<MCQOption> {
    const url = `${this.apiUrl}/${id}`;
    return this.http.put<MCQOption>(url, mcqOption);
  }

  // Delete an MCQ Option by ID
  deleteMCQOption(id: number): Observable<void> {
    const url = `${this.apiUrl}/${id}`;
    return this.http.delete<void>(url);
  }

  // Get an MCQ Option by ID
  getMCQOptionById(id: number): Observable<MCQOption> {
    const url = `${this.apiUrl}/${id}`;
    return this.http.get<MCQOption>(url);
  }

  // Get all MCQ Options with pagination
  getAllMCQOptions(page: number, size: number): Observable<Page<MCQOption>> {
    const params = new HttpParams()
      .set('page', page.toString())
      .set('size', size.toString());
    return this.http.get<Page<MCQOption>>(this.apiUrl, { params });
  }

  // Search MCQ Options by text with pagination
  searchMCQOptions(optionText: string, page: number, size: number): Observable<Page<MCQOption>> {
    const params = new HttpParams()
      .set('optionText', optionText)
      .set('page', page.toString())
      .set('size', size.toString());
    const url = `${this.apiUrl}/search`;
    return this.http.get<Page<MCQOption>>(url, { params });
  }


  getOptionsByQuestionId(questionId: number): Observable<MCQOption[]> {
    const url = `${this.apiUrl}/${questionId}/options`;
    return this.http.get<MCQOption[]>(url);
  }

  getOptionsByMultipleQuestionIds(questionIds: number[]): Observable<{ [key: number]: MCQOption[] }> {
    const url = `${this.apiUrl}/options-by-ids`;
    return this.http.post<{ [key: number]: MCQOption[] }>(url, questionIds);
  }
}
