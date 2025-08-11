import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Episode, Season, EpisodePage } from '../interfaces/content.interface';

@Injectable({
  providedIn: 'root'
})
export class EpisodeService {
  private readonly API_BASE_URL = 'http://localhost:8080/api/v1/episodes';

  constructor(private http: HttpClient) {}

  getSeasonsByContentId(contentId: string): Observable<Number[]> {
    return this.http.get<Number[]>(`${this.API_BASE_URL}/by-content/${contentId}/seasons`);
  }

  getEpisodesByContentAndSeason(contentId: string, seasonNumber: number, page: number = 0, size: number = 10): Observable<EpisodePage> {
    const params = new HttpParams()
      .set('page', page.toString())
      .set('size', size.toString());

    return this.http.get<EpisodePage>(`${this.API_BASE_URL}/by-content/${contentId}/season/${seasonNumber}`, { params });
  }

  getEpisodeByContentSeasonAndNumber(contentId: string, seasonNumber: number, episodeNumber: number): Observable<Episode> {
    return this.http.get<Episode>(`${this.API_BASE_URL}/by-content/${contentId}/season/${seasonNumber}/episode/${episodeNumber}`);
  }

  getEpisodeById(episodeId: string): Observable<Episode> {
    return this.http.get<Episode>(`${this.API_BASE_URL}/${episodeId}`);
  }
}
