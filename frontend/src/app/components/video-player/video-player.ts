import {Component, OnInit, signal} from '@angular/core';
import {ActivatedRoute, Router} from '@angular/router';
import {CommonModule, Location} from '@angular/common';
import {MatButtonModule} from '@angular/material/button';
import {MatIconModule} from '@angular/material/icon';
import {MatSnackBar, MatSnackBarModule} from '@angular/material/snack-bar';
import {ContentService} from '../../services/content.service';
import {EpisodeService} from '../../services/episode.service';
import {AuthService} from '../../services/auth.service';
import {Content, Episode} from '../../interfaces/content.interface';

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
  episode = signal<Episode | null>(null);
  isLoading = signal<boolean>(true);
  contentId = signal<string>('');

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private location: Location,
    private contentService: ContentService,
    private episodeService: EpisodeService,
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

    // First try to load as content
    this.contentService.getContentById(contentId).subscribe({
      next: (response) => {
        this.content.set(response);
        this.episode.set(null);
        this.isLoading.set(false);
      },
      error: (contentError) => {
        console.error('Error loading content:', contentError);

        // If content loading fails, try loading as episode
        this.episodeService.getEpisodeById(contentId).subscribe({
          next: (response) => {
            this.episode.set(response);
            this.content.set(null);
            this.isLoading.set(false);
          },
          error: (episodeError) => {
            console.error('Error loading episode:', episodeError);
            this.isLoading.set(false);
            this.snackBar.open('Failed to load video content. Please try again.', 'Close', {
              duration: 5000,
              panelClass: ['error-snackbar']
            });
            // Redirect back if both content and episode can't be loaded
            this.goBack();
          }
        });
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
