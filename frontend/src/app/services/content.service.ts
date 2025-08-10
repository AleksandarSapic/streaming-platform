import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Content, ContentPage } from '../interfaces/content.interface';

@Injectable({
  providedIn: 'root'
})
export class ContentService {
  private readonly API_BASE_URL = 'http://localhost:8080/api/v1/content';

  constructor(private http: HttpClient) {}

  getPopularContent(page: number = 0, size: number = 6): Observable<ContentPage> {
    const params = new HttpParams()
      .set('page', page.toString())
      .set('size', size.toString());
    
    return this.http.get<ContentPage>(`${this.API_BASE_URL}/popular`, { params });
  }

  getContentById(id: string): Observable<Content> {
    return this.http.get<Content>(`${this.API_BASE_URL}/${id}`);
  }

  getAvailableContent(page: number = 0, size: number = 10): Observable<ContentPage> {
    const params = new HttpParams()
      .set('page', page.toString())
      .set('size', size.toString());
    
    return this.http.get<ContentPage>(`${this.API_BASE_URL}/available`, { params });
  }

  searchContent(title: string, page: number = 0, size: number = 10): Observable<ContentPage> {
    const params = new HttpParams()
      .set('title', title)
      .set('page', page.toString())
      .set('size', size.toString());
    
    return this.http.get<ContentPage>(`${this.API_BASE_URL}/search`, { params });
  }

  getRecentContent(page: number = 0, size: number = 6): Observable<ContentPage> {
    const params = new HttpParams()
      .set('page', page.toString())
      .set('size', size.toString());
    
    return this.http.get<ContentPage>(`${this.API_BASE_URL}/recent`, { params });
  }

  getContentByType(type: string, page: number = 0, size: number = 10): Observable<ContentPage> {
    const params = new HttpParams()
      .set('type', type)
      .set('page', page.toString())
      .set('size', size.toString());
    
    return this.http.get<ContentPage>(`${this.API_BASE_URL}/by-type`, { params });
  }

  getContentByGenre(genre: string, page: number = 0, size: number = 10): Observable<ContentPage> {
    const params = new HttpParams()
      .set('genre', genre)
      .set('page', page.toString())
      .set('size', size.toString());
    
    return this.http.get<ContentPage>(`${this.API_BASE_URL}/by-genre`, { params });
  }
}