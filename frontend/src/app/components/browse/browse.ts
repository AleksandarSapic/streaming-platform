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
import {CommonModule} from '@angular/common';
import {AuthService} from '../../services/auth.service';
import {ContentService} from '../../services/content.service';
import {WatchlistService} from '../../services/watchlist.service';
import {ContentSection} from '../content-section/content-section';
import {Content, ContentPage} from '../../interfaces/content.interface';

@Component({
  selector: 'app-browse',
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
    ContentSection,
  ],
  templateUrl: './browse.html',
  styleUrl: './browse.css'
})
export class Browse implements OnInit {
  popularContent = signal<Content[]>([]);
  currentPage = signal<number>(0);
  totalPages = signal<number>(0);
  isLoading = signal<boolean>(false);

  recentContent = signal<Content[]>([]);
  recentCurrentPage = signal<number>(0);
  recentTotalPages = signal<number>(0);
  recentIsLoading = signal<boolean>(false);

  constructor(
    private authService: AuthService,
    private router: Router,
    private contentService: ContentService,
    private watchlistService: WatchlistService,
    private snackBar: MatSnackBar
  ) {
  }

  ngOnInit() {
    this.loadPopularContent();
    this.loadRecentContent();
  }

  get currentUser() {
    return this.authService.currentUser
  }

  loadPopularContent(page: number = 0) {
    this.isLoading.set(true);
    this.contentService.getPopularContent(page, 6).subscribe({
      next: (response: ContentPage) => {
        this.popularContent.set(response.content);
        this.currentPage.set(response.number);
        this.totalPages.set(response.totalPages);
        this.isLoading.set(false);
      },
      error: (error) => {
        console.error('Error loading popular content:', error);
        this.isLoading.set(false);
      }
    });
  }

  nextPage() {
    if (this.currentPage() < this.totalPages() - 1) {
      const nextPage = this.currentPage() + 1;
      this.loadPopularContent(nextPage);
    }
  }

  previousPage() {
    if (this.currentPage() > 0) {
      const prevPage = this.currentPage() - 1;
      this.loadPopularContent(prevPage);
    }
  }

  loadRecentContent(page: number = 0) {
    this.recentIsLoading.set(true);
    this.contentService.getRecentContent(page, 6).subscribe({
      next: (response: ContentPage) => {
        this.recentContent.set(response.content);
        this.recentCurrentPage.set(response.number);
        this.recentTotalPages.set(response.totalPages);
        this.recentIsLoading.set(false);
      },
      error: (error) => {
        console.error('Error loading recent content:', error);
        this.recentIsLoading.set(false);
      }
    });
  }

  nextRecentPage() {
    if (this.recentCurrentPage() < this.recentTotalPages() - 1) {
      const nextPage = this.recentCurrentPage() + 1;
      this.loadRecentContent(nextPage);
    }
  }

  previousRecentPage() {
    if (this.recentCurrentPage() > 0) {
      const prevPage = this.recentCurrentPage() - 1;
      this.loadRecentContent(prevPage);
    }
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
        this.snackBar.open('Content added to your list successfully!', 'Close', {
          duration: 3000,
          panelClass: ['success-snackbar']
        });
      },
      error: (error) => {
        console.error('Error adding content to watchlist:', error);

        const errorMessage = error.error?.message;
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
