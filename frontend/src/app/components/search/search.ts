import {Component, OnInit, signal} from '@angular/core';
import {Router} from '@angular/router';
import {CommonModule} from '@angular/common';
import {FormsModule} from '@angular/forms';
import {MatInputModule} from '@angular/material/input';
import {MatFormFieldModule} from '@angular/material/form-field';
import {MatIconModule} from '@angular/material/icon';
import {MatButtonModule} from '@angular/material/button';
import {MatSnackBar, MatSnackBarModule} from '@angular/material/snack-bar';
import {debounceTime, distinctUntilChanged, Subject} from 'rxjs';
import {AuthService} from '../../services/auth.service';
import {ContentService} from '../../services/content.service';
import {WatchlistService} from '../../services/watchlist.service';
import {ContentSection} from '../content-section/content-section';
import {Content, ContentPage} from '../../interfaces/content.interface';

@Component({
  selector: 'app-search',
  imports: [
    CommonModule,
    FormsModule,
    MatInputModule,
    MatFormFieldModule,
    MatIconModule,
    MatButtonModule,
    MatSnackBarModule,
    ContentSection,
  ],
  templateUrl: './search.html',
  styleUrl: './search.css'
})
export class Search implements OnInit {
  searchQuery = signal<string>('');
  searchResults = signal<Content[]>([]);
  currentPage = signal<number>(0);
  totalPages = signal<number>(0);
  isLoading = signal<boolean>(false);
  hasSearched = signal<boolean>(false);

  private searchSubject = new Subject<string>();

  constructor(
    private authService: AuthService,
    private router: Router,
    private contentService: ContentService,
    private watchlistService: WatchlistService,
    private snackBar: MatSnackBar
  ) {
  }

  ngOnInit() {
    this.searchSubject
      .pipe(
        debounceTime(500),
        distinctUntilChanged()
      )
      .subscribe(query => {
        if (query.trim()) {
          this.performSearch(query.trim());
        } else {
          this.clearResults();
        }
      });
  }

  get currentUser() {
    return this.authService.currentUser;
  }

  onSearchInput(query: string) {
    this.searchQuery.set(query);
    this.searchSubject.next(query);
  }

  performSearch(query: string, page: number = 0) {
    this.isLoading.set(true);
    this.hasSearched.set(true);

    this.contentService.searchContent(query, page, 12).subscribe({
      next: (response: ContentPage) => {
        this.searchResults.set(response.content);
        this.currentPage.set(response.number);
        this.totalPages.set(response.totalPages);
        this.isLoading.set(false);
      },
      error: (error) => {
        console.error('Error searching content:', error);
        this.isLoading.set(false);
        this.snackBar.open('Failed to search content. Please try again.', 'Close', {
          duration: 5000,
          panelClass: ['error-snackbar']
        });
      }
    });
  }

  clearResults() {
    this.searchResults.set([]);
    this.hasSearched.set(false);
    this.currentPage.set(0);
    this.totalPages.set(0);
  }

  nextPage() {
    const query = this.searchQuery().trim();
    if (this.currentPage() < this.totalPages() - 1 && query) {
      const nextPage = this.currentPage() + 1;
      this.performSearch(query, nextPage);
    }
  }

  previousPage() {
    const query = this.searchQuery().trim();
    if (this.currentPage() > 0 && query) {
      const prevPage = this.currentPage() - 1;
      this.performSearch(query, prevPage);
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
        const errorMessage = error.error?.message || 'Failed to add content to your list. Please try again.';
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
