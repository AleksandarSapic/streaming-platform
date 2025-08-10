import {Component, OnInit, signal} from '@angular/core';
import {Router} from '@angular/router';
import {MatToolbarModule} from '@angular/material/toolbar';
import {MatButtonModule} from '@angular/material/button';
import {MatIconModule} from '@angular/material/icon';
import {MatMenuModule} from '@angular/material/menu';
import {MatInputModule} from '@angular/material/input';
import {MatFormFieldModule} from '@angular/material/form-field';
import {MatDividerModule} from '@angular/material/divider';
import {MatCardModule} from '@angular/material/card';
import {MatSnackBar, MatSnackBarModule} from '@angular/material/snack-bar';
import {MatChipsModule} from '@angular/material/chips';
import {CommonModule} from '@angular/common';
import {AuthService} from '../../services/auth.service';
import {ContentService} from '../../services/content.service';
import {WatchlistService} from '../../services/watchlist.service';
import {GenreService} from '../../services/genre.service';
import {ContentSection} from '../content-section/content-section';
import {Content, ContentPage, Genre} from '../../interfaces/content.interface';

@Component({
  selector: 'app-movies',
  imports: [
    CommonModule,
    MatToolbarModule,
    MatButtonModule,
    MatIconModule,
    MatMenuModule,
    MatInputModule,
    MatFormFieldModule,
    MatDividerModule,
    MatCardModule,
    MatSnackBarModule,
    MatChipsModule,
    ContentSection,
  ],
  templateUrl: './movies.html',
  styleUrl: './movies.css'
})
export class Movies implements OnInit {
  genres = signal<Genre[]>([]);
  selectedGenre = signal<string | null>(null);

  actionMovies = signal<Content[]>([]);
  actionCurrentPage = signal<number>(0);
  actionTotalPages = signal<number>(0);
  actionIsLoading = signal<boolean>(false);

  dramaMovies = signal<Content[]>([]);
  dramaCurrentPage = signal<number>(0);
  dramaTotalPages = signal<number>(0);
  dramaIsLoading = signal<boolean>(false);

  filteredContent = signal<Content[]>([]);
  filteredCurrentPage = signal<number>(0);
  filteredTotalPages = signal<number>(0);
  filteredIsLoading = signal<boolean>(false);
  selectedGenreName = signal<string>('');

  constructor(
    private authService: AuthService,
    private router: Router,
    private contentService: ContentService,
    private watchlistService: WatchlistService,
    private genreService: GenreService,
    private snackBar: MatSnackBar
  ) {
  }

  ngOnInit() {
    this.loadGenres();
    this.loadActionMovies();
    this.loadDramaMovies();
  }

  get currentUser() {
    return this.authService.currentUser;
  }

  loadGenres() {
    this.genreService.getAllGenres(0, 20).subscribe({
      next: (response) => {
        this.genres.set(response.content);
      },
      error: (error) => {
        console.error('Error loading genres:', error);
        this.snackBar.open('Failed to load genres. Please refresh the page to try again.', 'Close', {
          duration: 5000,
          panelClass: ['error-snackbar']
        });
        this.genres.set([]);
      }
    });
  }

  onGenreSelected(genreId: string | null) {
    this.selectedGenre.set(genreId);

    if (genreId === null) {
      this.selectedGenreName.set('');
      return;
    }

    const genre = this.genres().find(g => g.id === genreId);
    if (genre) {
      this.selectedGenreName.set(genre.name);
      this.loadFilteredContent(genre.name);
    }
  }

  loadActionMovies(page: number = 0) {
    this.actionIsLoading.set(true);
    this.contentService.getContentByGenre('Action', page, 6).subscribe({
      next: (response: ContentPage) => {
        this.actionMovies.set(response.content);
        this.actionCurrentPage.set(response.number);
        this.actionTotalPages.set(response.totalPages);
        this.actionIsLoading.set(false);
      },
      error: (error) => {
        console.error('Error loading action movies:', error);
        this.actionIsLoading.set(false);
      }
    });
  }

  nextActionPage() {
    if (this.actionCurrentPage() < this.actionTotalPages() - 1) {
      const nextPage = this.actionCurrentPage() + 1;
      this.loadActionMovies(nextPage);
    }
  }

  previousActionPage() {
    if (this.actionCurrentPage() > 0) {
      const prevPage = this.actionCurrentPage() - 1;
      this.loadActionMovies(prevPage);
    }
  }

  loadDramaMovies(page: number = 0) {
    this.dramaIsLoading.set(true);
    this.contentService.getContentByGenre('Drama', page, 6).subscribe({
      next: (response: ContentPage) => {
        this.dramaMovies.set(response.content);
        this.dramaCurrentPage.set(response.number);
        this.dramaTotalPages.set(response.totalPages);
        this.dramaIsLoading.set(false);
      },
      error: (error) => {
        console.error('Error loading drama movies:', error);
        this.dramaIsLoading.set(false);
      }
    });
  }

  nextDramaPage() {
    if (this.dramaCurrentPage() < this.dramaTotalPages() - 1) {
      const nextPage = this.dramaCurrentPage() + 1;
      this.loadDramaMovies(nextPage);
    }
  }

  previousDramaPage() {
    if (this.dramaCurrentPage() > 0) {
      const prevPage = this.dramaCurrentPage() - 1;
      this.loadDramaMovies(prevPage);
    }
  }

  loadFilteredContent(genreName: string, page: number = 0) {
    this.filteredIsLoading.set(true);
    this.contentService.getContentByGenre(genreName, page, 6).subscribe({
      next: (response: ContentPage) => {
        this.filteredContent.set(response.content);
        this.filteredCurrentPage.set(response.number);
        this.filteredTotalPages.set(response.totalPages);
        this.filteredIsLoading.set(false);
      },
      error: (error) => {
        console.error('Error loading filtered content:', error);
        this.filteredIsLoading.set(false);
      }
    });
  }

  nextFilteredPage() {
    if (this.filteredCurrentPage() < this.filteredTotalPages() - 1) {
      const nextPage = this.filteredCurrentPage() + 1;
      const genreName = this.selectedGenreName();
      if (genreName) {
        this.loadFilteredContent(genreName, nextPage);
      }
    }
  }

  previousFilteredPage() {
    if (this.filteredCurrentPage() > 0) {
      const prevPage = this.filteredCurrentPage() - 1;
      const genreName = this.selectedGenreName();
      if (genreName) {
        this.loadFilteredContent(genreName, prevPage);
      }
    }
  }

  isAllMoviesSelected(): boolean {
    return this.selectedGenre() === null || this.selectedGenre() === '';
  }

  addToMyList(contentId: string) {
    const currentUser = this.currentUser();
    if (!currentUser?.id) {
      this.snackBar.open('Please log in to add content to your list', 'Close', {
        duration: 4000,
        panelClass: ['error-snackbar']
      });
      return;
    }

    this.watchlistService.addToWatchlist(currentUser.id, contentId).subscribe({
      next: () => {
        this.snackBar.open('Movie added to your list successfully!', 'Close', {
          duration: 3000,
          panelClass: ['success-snackbar']
        });
      },
      error: (error) => {
        console.error('Error adding movie to watchlist:', error);

        const errorMessage = error.error?.message || 'Failed to add movie to your list. Please try again.';
        this.snackBar.open(errorMessage, 'Close', {
          duration: 5000,
          panelClass: ['error-snackbar']
        });
      }
    });
  }

  navigateToContentDetail(contentId: string) {
    this.router.navigate(['/content', contentId]);
  }
}
