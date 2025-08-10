import {Component, OnInit, signal} from '@angular/core';
import {ActivatedRoute, Router} from '@angular/router';
import {CommonModule} from '@angular/common';
import {MatCardModule} from '@angular/material/card';
import {MatButtonModule} from '@angular/material/button';
import {MatIconModule} from '@angular/material/icon';
import {MatChipsModule} from '@angular/material/chips';
import {MatSnackBar, MatSnackBarModule} from '@angular/material/snack-bar';
import {ContentService} from '../../services/content.service';
import {WatchlistService} from '../../services/watchlist.service';
import {AuthService} from '../../services/auth.service';
import {Content} from '../../interfaces/content.interface';

@Component({
  selector: 'app-content-detail',
  imports: [
    CommonModule,
    MatCardModule,
    MatButtonModule,
    MatIconModule,
    MatChipsModule,
    MatSnackBarModule,
  ],
  templateUrl: './content-detail.html',
  styleUrl: './content-detail.css'
})
export class ContentDetail implements OnInit {
  content = signal<Content | null>(null);
  isLoading = signal<boolean>(true);
  contentId = signal<string>('');

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private contentService: ContentService,
    private watchlistService: WatchlistService,
    private authService: AuthService,
    private snackBar: MatSnackBar
  ) {
  }

  ngOnInit() {
    this.route.params.subscribe(params => {
      const id = params['id'];
      if (id) {
        this.contentId.set(id);
        this.loadContentDetail(id);
      }
    });
  }

  get currentUser() {
    return this.authService.currentUser;
  }

  loadContentDetail(contentId: string) {
    this.isLoading.set(true);
    this.contentService.getContentById(contentId).subscribe({
      next: (response) => {
        this.content.set(response);
        this.isLoading.set(false);
      },
      error: (error) => {
        console.error('Error loading content details:', error);
        this.isLoading.set(false);
        this.snackBar.open('Failed to load content details. Please try again.', 'Close', {
          duration: 5000,
          panelClass: ['error-snackbar']
        });
      }
    });
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
    this.router.navigate(['/browse']);
  }
}
