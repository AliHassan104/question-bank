import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { map, Observable } from 'rxjs';
import { Page } from '../models/page.model'; // For pagination model
import { environment } from '../../environments/environment'; // For base API URL
import { Question } from 'app/models/question.model';


@Injectable({
  providedIn: 'root'
})
export class QuestionService {

  private apiUrl = `${environment.apiUrl}/api/questions`;

  constructor(private http: HttpClient) { }

  // Create a new Question
  createQuestion(question: Question): Observable<Question> {
    return this.http.post<Question>(this.apiUrl, question);
  }

  // Update a Question by ID
  updateQuestion(id: number, question: Question): Observable<Question> {
    const url = `${this.apiUrl}/${id}`;
    return this.http.put<Question>(url, question);
  }

  // Delete a Question by ID
  deleteQuestion(id: number): Observable<void> {
    const url = `${this.apiUrl}/${id}`;
    return this.http.delete<void>(url);
  }

  // Get a Question by ID
  getQuestionById(id: number): Observable<Question> {
    const url = `${this.apiUrl}/${id}`;
    return this.http.get<Question>(url);
  }

  // Get all Questions with pagination
  getAllQuestions(page: number, size: number): Observable<Question[]> {
    const params = new HttpParams()
    //   .set('page', page.toString())
    //   .set('size', size.toString());
    return this.http.get<Question[]>(this.apiUrl, { params });
  }

  // Search Questions by text with pagination
  searchQuestions(questionText: string, page: number, size: number): Observable<Question[]> {
    const params = new HttpParams()
      .set('questionText', questionText)
      .set('page', page.toString())
      .set('size', size.toString());
    const url = `${this.apiUrl}/search`;
    return this.http.get<Question[]>(url, { params });
  }

  // Get filtered Questions with pagination
  getFilteredQuestions(
    sectionType?: string,
    chapterId?: number,
    subjectId?: number,
    classId?: number,
    page: number = 0,
    size: number = 10
  ): Observable<Page<Question>> {
    let params = new HttpParams()
      .set('page', page.toString())
      .set('size', size.toString());

    if (sectionType) {
      params = params.set('sectionType', sectionType);
    }
    if (chapterId) {
      params = params.set('chapterId', chapterId.toString());
    }
    if (subjectId) {
      params = params.set('subjectId', subjectId.toString());
    }
    if (classId) {
      params = params.set('classId', classId.toString());
    }

    const url = `${this.apiUrl}/filter`;
    return this.http.get<Page<Question>>(url, { params });
  }

  // Toggle the "isAddedToPaper" status of a Question by ID
  toggleAddedToPaper(id: number): Observable<Question> {
    const url = `${this.apiUrl}/${id}/toggle-paper-status`;
    return this.http.patch<Question>(url, null);  // Patch request with no body
  }

  // Get Questions by Subject ID
  getQuestionsBySubjectId(subjectId: number): Observable<Question[]> {
    const url = `${this.apiUrl}/subject/${subjectId}`;
    return this.http.get<Question[]>(url);
  }

  // Get Questions by Subject ID and only those added to paper
  getQuestionsBySubjectIdAndAddedToPaper(subjectId: number): Observable<Question[]> {
    const url = `${this.apiUrl}/subject/${subjectId}/added-to-paper`;
    return this.http.get<Question[]>(url);
  }

  // generatePaper(subjectId: number): Observable<void> {
  //   const url = `${this.apiUrl}/question-bank/pdf/${subjectId}`;

  //   return this.http.get(url, { responseType: 'blob' }).pipe(
  //     map((pdfBlob: Blob) => {
  //       // Create a URL for the Blob object
  //       const pdfUrl = window.URL.createObjectURL(pdfBlob);

  //       // Create an anchor element to trigger download
  //       const anchor = document.createElement('a');
  //       anchor.href = pdfUrl;
  //       anchor.download = 'question-bank.pdf';  // Name of the PDF file to download

  //       // Trigger the download
  //       anchor.click();

  //       // Clean up the URL object
  //       window.URL.revokeObjectURL(pdfUrl);
  //     })
  //   );
  // }

  generatePaper(subjectId: number): Observable<void> {
  const url = `http://localhost:8080/api/exam/report/${subjectId}?format=pdf`;
  return this.http.get(url, { responseType: 'blob' }).pipe(
    map((blob: Blob) => {
      const fileURL = URL.createObjectURL(blob);
      const fileName = `exam-paper.pdf`;

      const anchor = document.createElement('a');
      anchor.href = fileURL;
      anchor.download = fileName;
      anchor.click();

      URL.revokeObjectURL(fileURL);
    })
  );
}

generateSamplePaper(): Observable<void> {
  const url = `http://localhost:8080/api/questions/sample-report`;
  return this.http.get(url, { responseType: 'blob' }).pipe(
    map((blob: Blob) => {
      const fileURL = URL.createObjectURL(blob);
      const fileName = `exam-paper.pdf`;

      const anchor = document.createElement('a');
      anchor.href = fileURL;
      anchor.download = fileName;
      anchor.click();

      URL.revokeObjectURL(fileURL);
    })
  );
}


}
