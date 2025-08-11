import {Component, OnInit, signal} from '@angular/core';
import {ActivatedRoute, Router} from '@angular/router';
import {CommonModule, Location} from '@angular/common';
import {MatButtonModule} from '@angular/material/button';
import {MatIconModule} from '@angular/material/icon';
import {MatSnackBar, MatSnackBarModule} from '@angular/material/snack-bar';
import {ContentService} from '../../services/content.service';
import {AuthService} from '../../services/auth.service';
import {Content} from '../../interfaces/content.interface';

@Component({
  selector: 'app-video-player',
  imports: [
    CommonModule,
    MatButtonModule,
    MatIconModule,
    MatSnackBarModule
  ],
  templateUrl: './video-player.html',
  styleUrl: './video-player.css'
})
export class VideoPlayer implements OnInit {
  content = signal<Content | null>(null);
  isLoading = signal<boolean>(true);
  contentId = signal<string>('');

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private location: Location,
    private contentService: ContentService,
    private authService: AuthService,
    private snackBar: MatSnackBar
  ) {}

  ngOnInit() {
    this.route.params.subscribe(params => {
      const id = params['id'];
      if (id) {
        this.contentId.set(id);
        this.loadContent(id);
      }
    });
  }

  get currentUser() {
    return this.authService.currentUser;
  }

  loadContent(contentId: string) {
    this.isLoading.set(true);
    this.contentService.getContentById(contentId).subscribe({
      next: (response) => {
        this.content.set(response);
        this.isLoading.set(false);
      },
      error: (error) => {
        console.error('Error loading content:', error);
        this.isLoading.set(false);
        this.snackBar.open('Failed to load video content. Please try again.', 'Close', {
          duration: 5000,
          panelClass: ['error-snackbar']
        });
        // Redirect back if content can't be loaded
        this.goBack();
      }
    });
  }

  goBack() {
    this.location.back();
  }

  goToDetails() {
    const contentId = this.contentId();
    if (contentId) {
      this.router.navigate(['/content', contentId]);
    }
  }
}
