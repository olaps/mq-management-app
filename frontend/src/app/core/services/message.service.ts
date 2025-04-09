// message.service.ts
import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Message, Page } from '../models/message.model';
import { environment } from '../../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class MessageService {
  private apiUrl = `${environment.apiUrl}/messages`;

  constructor(private http: HttpClient) {
    console.log('API URL:', `${environment.apiUrl}/messages`); // Log pour débugger
  }
  
  getMessages(page = 0, size = 20, sort = 'receivedAt,desc'): Observable<Page<Message>> {
    let params = new HttpParams()
      .set('page', page.toString())
      .set('size', size.toString())
      .set('sort', sort);

    return this.http.get<Page<Message>>(this.apiUrl, { params });
  }

  getMessage(id: number): Observable<Message> {
    return this.http.get<Message>(`${this.apiUrl}/${id}`);
  }

  getMessagesByProcessed(processed: boolean, page = 0, size = 20): Observable<Page<Message>> {
    let params = new HttpParams()
      .set('page', page.toString())
      .set('size', size.toString());

    return this.http.get<Page<Message>>(`${this.apiUrl}/processed/${processed}`, { params });
  }

  getMessagesByDateRange(startDate: string, endDate: string, page = 0, size = 20): Observable<Page<Message>> {
    let params = new HttpParams()
      .set('startDate', startDate)
      .set('endDate', endDate)
      .set('page', page.toString())
      .set('size', size.toString());

    return this.http.get<Page<Message>>(`${this.apiUrl}/dateRange`, { params });
  }

  getMessagesByQueueName(queueName: string, page = 0, size = 20): Observable<Page<Message>> {
    let params = new HttpParams()
      .set('page', page.toString())
      .set('size', size.toString());

    return this.http.get<Page<Message>>(`${this.apiUrl}/queue/${queueName}`, { params });
  }

  searchMessages(keyword: string, page = 0, size = 20): Observable<Page<Message>> {
    let params = new HttpParams()
      .set('keyword', keyword)
      .set('page', page.toString())
      .set('size', size.toString());

    return this.http.get<Page<Message>>(`${this.apiUrl}/search`, { params });
  }
}