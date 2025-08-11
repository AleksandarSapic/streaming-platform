import {Component, OnInit, signal} from '@angular/core';
import {ActivatedRoute, Router} from '@angular/router';
import {CommonModule, Location} from '@angular/common';
import {MatCardModule} from '@angular/material/card';
import {MatButtonModule} from '@angular/material/button';
import {MatIconModule} from '@angular/material/icon';
import {MatChipsModule} from '@angular/material/chips';
import {MatSnackBar, MatSnackBarModule} from '@angular/material/snack-bar';
import {MatSelectModule} from '@angular/material/select';
import {MatFormFieldModule} from '@angular/material/form-field';
import {ContentService} from '../../services/content.service';
import {WatchlistService} from '../../services/watchlist.service';
import {AuthService} from '../../services/auth.service';
import {EpisodeService} from '../../services/episode.service';
import {Content, Episode, EpisodePage} from '../../interfaces/content.interface';
import {ContentSection} from '../content-section/content-section';

@Component({
  selector: 'app-show-detail',
  imports: [
    CommonModule,
    MatCardModule,
    MatButtonModule,
    MatIconModule,
    MatChipsModule,
    MatSnackBarModule,
    MatSelectModule,
    MatFormFieldModule,
    ContentSection
  ],
  templateUrl: './show-detail.html',
  styleUrl: './show-detail.css'
})
export class ShowDetail implements OnInit {
  content = signal<Content | null>(null);
  seasons = signal<Number[]>([]);
  selectedSeason = signal<number>(1);
  episodes = signal<Episode[]>([]);
  episodePage = signal<EpisodePage | null>(null);
  currentPage = signal<number>(0);
  isLoading = signal<boolean>(true);
  isLoadingEpisodes = signal<boolean>(false);
  contentId = signal<string>('');

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private location: Location,
    private contentService: ContentService,
    private watchlistService: WatchlistService,
    private authService: AuthService,
    private episodeService: EpisodeService,
    private snackBar: MatSnackBar
  ) {
  }

  ngOnInit() {
    this.route.params.subscribe(params => {
      const id = params['id'];
      if (id) {
        this.contentId.set(id);
        this.loadShowDetail(id);
        this.loadSeasons(id);
        this.loadEpisodes(id, 1, 0);
      }
    });
  }

  get currentUser() {
    return this.authService.currentUser;
  }

  loadShowDetail(contentId: string) {
    this.isLoading.set(true);
    this.contentService.getContentById(contentId).subscribe({
      next: (response) => {
        this.content.set(response);
        this.isLoading.set(false);
      },
      error: (error) => {
        console.error('Error loading show details:', error);
        this.isLoading.set(false);
        this.snackBar.open('Failed to load show details. Please try again.', 'Close', {
          duration: 5000,
          panelClass: ['error-snackbar']
        });
      }
    });
  }

  loadSeasons(contentId: string) {
    this.episodeService.getSeasonsByContentId(contentId).subscribe({
      next: (response) => {
        this.seasons.set(response);
      },
      error: (error) => {
        console.error('Error loading seasons:', error);
      }
    });
  }

  loadEpisodes(contentId: string, season: number, page: number = 0) {
    this.isLoadingEpisodes.set(true);
    this.episodeService.getEpisodesByContentAndSeason(contentId, season, page).subscribe({
      next: (response) => {
        this.episodePage.set(response);
        this.episodes.set(response.content);
        this.currentPage.set(response.number);
        this.isLoadingEpisodes.set(false);
      },
      error: (error) => {
        console.error('Error loading episodes:', error);
        this.isLoadingEpisodes.set(false);
        this.snackBar.open('Failed to load episodes. Please try again.', 'Close', {
          duration: 5000,
          panelClass: ['error-snackbar']
        });
      }
    });
  }

  onSeasonChange(seasonNumber: number) {
    this.selectedSeason.set(seasonNumber);
    this.currentPage.set(0);
    this.loadEpisodes(this.contentId(), seasonNumber, 0);
  }

  addToMyList() {
    const currentUser = this.currentUser();
    const contentId = this.contentId();

    if (!currentUser?.id) {
      this.snackBar.open('Please log in to add content to your list', 'Close', {
        duration: 4000,
        panelClass: ['error-snackbar']
      });
      return;
    }

    this.watchlistService.addToWatchlist(currentUser.id, contentId).subscribe({
      next: () => {
        this.snackBar.open('Content added to your list successfully!', 'Close', {
          duration: 3000,
          panelClass: ['success-snackbar']
        });
      },
      error: (error) => {
        console.error('Error adding content to watchlist:', error);
        const errorMessage = error.error?.message || 'Failed to add content to your list. Please try again.';
        this.snackBar.open(errorMessage, 'Close', {
          duration: 5000,
          panelClass: ['error-snackbar']
        });
      }
    });
  }

  goBack() {
    this.location.back();
  }

  watchSeason1Episode1() {
    const firstEpisode = this.episodes()[0];
    if (firstEpisode) {
      this.router.navigate(['/watch', firstEpisode.id]);
    }
  }

  onEpisodePreviousPage() {
    const currentPage = this.currentPage();
    if (currentPage > 0) {
      this.loadEpisodes(this.contentId(), this.selectedSeason(), currentPage - 1);
    }
  }

  onEpisodeNextPage() {
    const episodePage = this.episodePage();
    const currentPage = this.currentPage();
    if (episodePage && currentPage < episodePage.totalPages - 1) {
      this.loadEpisodes(this.contentId(), this.selectedSeason(), currentPage + 1);
    }
  }

  onEpisodeMoreInfo(episodeId: string) {
    this.router.navigate(['/episode', episodeId]);
  }

  onEpisodePlay(episodeId: string) {
    const episode = this.episodes().find(ep => ep.id === episodeId);
    if (episode) {
      this.router.navigate(['/watch', episodeId]);
    }
  }

  getTotalSeasons(): number {
    return this.seasons().length;
  }
}
