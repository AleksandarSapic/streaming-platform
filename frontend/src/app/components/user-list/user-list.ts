import {Component, OnInit, signal} from '@angular/core';
import {Router} from '@angular/router';
import {CommonModule} from '@angular/common';
import {MatSnackBar, MatSnackBarModule} from '@angular/material/snack-bar';
import {AuthService} from '../../services/auth.service';
import {WatchlistService} from '../../services/watchlist.service';
import {ContentSection} from '../content-section/content-section';
import {Content, ContentPage} from '../../interfaces/content.interface';

@Component({
  selector: 'app-user-list',
  imports: [
    CommonModule,
    MatSnackBarModule,
    ContentSection,
  ],
  templateUrl: './user-list.html',
  styleUrl: './user-list.css'
})
export class UserList implements OnInit {
  watchlistContent = signal<Content[]>([]);
  currentPage = signal<number>(0);
  totalPages = signal<number>(0);
  isLoading = signal<boolean>(false);

  constructor(
    private authService: AuthService,
    private router: Router,
    private watchlistService: WatchlistService,
    private snackBar: MatSnackBar
  ) {
  }

  ngOnInit() {
    this.loadWatchlist();
  }

  get currentUser() {
    return this.authService.currentUser;
  }

  loadWatchlist(page: number = 0) {
    const currentUser = this.currentUser();
    if (!currentUser?.id) {
      this.snackBar.open('Please log in to view your watchlist', 'Close', {
        duration: 4000,
        panelClass: ['error-snackbar']
      });
      this.router.navigate(['/login']);
      return;
    }

    this.isLoading.set(true);
    this.watchlistService.getUserWatchlist(currentUser.id, page, 12).subscribe({
      next: (response: ContentPage) => {
        this.watchlistContent.set(response.content);
        this.currentPage.set(response.number);
        this.totalPages.set(response.totalPages);
        this.isLoading.set(false);
      },
      error: (error) => {
        console.error('Error loading watchlist:', error);
        this.isLoading.set(false);
        this.snackBar.open('Failed to load your watchlist. Please try again.', 'Close', {
          duration: 5000,
          panelClass: ['error-snackbar']
        });
      }
    });
  }

  nextPage() {
    if (this.currentPage() < this.totalPages() - 1) {
      const nextPage = this.currentPage() + 1;
      this.loadWatchlist(nextPage);
    }
  }

  previousPage() {
    if (this.currentPage() > 0) {
      const prevPage = this.currentPage() - 1;
      this.loadWatchlist(prevPage);
    }
  }

  removeFromList(contentId: string) {
    const currentUser = this.currentUser();
    if (!currentUser?.id) return;

    this.watchlistService.removeFromWatchlist(currentUser.id, contentId).subscribe({
      next: () => {
        this.snackBar.open('Removed from your list successfully!', 'Close', {
          duration: 3000,
          panelClass: ['success-snackbar']
        });
        this.loadWatchlist(this.currentPage());
      },
      error: (error) => {
        console.error('Error removing from watchlist:', error);
        const errorMessage = error.error?.message || 'Failed to remove from your list. Please try again.';
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

  logout() {
    this.authService.logout();
    this.router.navigate(['/login']);
  }
}
