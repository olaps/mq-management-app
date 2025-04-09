

// partner.service.ts
import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Partner } from '../models/partner.model';
import { Page } from '../models/message.model';
import { environment } from '../../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class PartnerService {
  private apiUrl = `${environment.apiUrl}/partners`;

  constructor(private http: HttpClient) {}

  getPartners(page = 0, size = 20, sort = 'alias,asc'): Observable<Page<Partner>> {
    let params = new HttpParams()
      .set('page', page.toString())
      .set('size', size.toString())
      .set('sort', sort);

    return this.http.get<Page<Partner>>(this.apiUrl, { params });
  }

  getPartner(id: number): Observable<Partner> {
    return this.http.get<Partner>(`${this.apiUrl}/${id}`);
  }

  getPartnerByAlias(alias: string): Observable<Partner> {
    return this.http.get<Partner>(`${this.apiUrl}/alias/${alias}`);
  }

  createPartner(partner: Partner): Observable<Partner> {
    return this.http.post<Partner>(this.apiUrl, partner);
  }

  updatePartner(id: number, partner: Partner): Observable<Partner> {
    return this.http.put<Partner>(`${this.apiUrl}/${id}`, partner);
  }

  deletePartner(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }

  searchPartners(keyword: string, page = 0, size = 20): Observable<Page<Partner>> {
    let params = new HttpParams()
      .set('keyword', keyword)
      .set('page', page.toString())
      .set('size', size.toString());

    return this.http.get<Page<Partner>>(`${this.apiUrl}/search`, { params });
  }
}