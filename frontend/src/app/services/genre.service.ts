import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Genre } from '../interfaces/content.interface';

export interface GenrePage {
  content: Genre[];
  totalElements: number;
  totalPages: number;
  number: number;
  size: number;
  first: boolean;
  last: boolean;
}

@Injectable({
  providedIn: 'root'
})
export class GenreService {
  private readonly API_BASE_URL = 'http://localhost:8080/api/v1/genres';

  constructor(private http: HttpClient) {}

  getAllGenres(page: number = 0, size: number = 100): Observable<GenrePage> {
    const params = new HttpParams()
      .set('page', page.toString())
      .set('size', size.toString());
    
    return this.http.get<GenrePage>(this.API_BASE_URL, { params });
  }

  getGenreById(id: string): Observable<Genre> {
    return this.http.get<Genre>(`${this.API_BASE_URL}/${id}`);
  }

  getGenreByName(name: string): Observable<Genre> {
    return this.http.get<Genre>(`${this.API_BASE_URL}/by-name/${name}`);
  }

  searchGenres(name: string, page: number = 0, size: number = 10): Observable<GenrePage> {
    const params = new HttpParams()
      .set('name', name)
      .set('page', page.toString())
      .set('size', size.toString());
    
    return this.http.get<GenrePage>(`${this.API_BASE_URL}/search`, { params });
  }

  getPopularGenres(page: number = 0, size: number = 10): Observable<GenrePage> {
    const params = new HttpParams()
      .set('page', page.toString())
      .set('size', size.toString());
    
    return this.http.get<GenrePage>(`${this.API_BASE_URL}/popular`, { params });
  }
}