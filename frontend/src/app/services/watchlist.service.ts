import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class WatchlistService {
  private readonly API_BASE_URL = 'http://localhost:8080/api/v1/watchlist';

  constructor(private http: HttpClient) {}

  addToWatchlist(userId: string, contentId: string): Observable<any> {
    const params = new HttpParams()
      .set('userId', userId)
      .set('contentId', contentId);
    
    return this.http.post(this.API_BASE_URL, null, { params });
  }

  removeFromWatchlist(userId: string, contentId: string): Observable<any> {
    const params = new HttpParams()
      .set('userId', userId)
      .set('contentId', contentId);
    
    return this.http.delete(this.API_BASE_URL, { params });
  }

  getUserWatchlist(userId: string, page: number = 0, size: number = 10): Observable<any> {
    const params = new HttpParams()
      .set('userId', userId)
      .set('page', page.toString())
      .set('size', size.toString());
    
    return this.http.get(this.API_BASE_URL, { params });
  }
}