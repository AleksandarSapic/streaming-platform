import {Component, EventEmitter, Input, Output} from '@angular/core';
import {MatButtonModule} from '@angular/material/button';
import {MatIconModule} from '@angular/material/icon';
import {MatMenuModule} from '@angular/material/menu';
import {MatCardModule} from '@angular/material/card';
import {CommonModule} from '@angular/common';
import {Router} from '@angular/router';
import {Content, Episode} from '../../interfaces/content.interface';

@Component({
  selector: 'app-content-section',
  imports: [
    CommonModule,
    MatButtonModule,
    MatIconModule,
    MatMenuModule,
    MatCardModule
  ],
  templateUrl: './content-section.html',
  styleUrl: './content-section.css'
})
export class ContentSection {
  @Input() title: string = '';
  @Input() content: (Content | Episode)[] = [];
  @Input() isLoading: boolean = false;
  @Input() currentPage: number = 0;
  @Input() totalPages: number = 0;
  @Input() isWatchlist: boolean = false;
  @Input() isEpisodeView: boolean = false;

  @Output() previousPage = new EventEmitter<void>();
  @Output() nextPage = new EventEmitter<void>();
  @Output() addToList = new EventEmitter<string>();
  @Output() moreInfo = new EventEmitter<string>();
  @Output() playContent = new EventEmitter<string>();

  constructor(private router: Router) {}

  canGoPrevious(): boolean {
    return this.currentPage > 0;
  }

  canGoNext(): boolean {
    return this.currentPage < this.totalPages - 1;
  }

  onPreviousPage() {
    this.previousPage.emit();
  }

  onNextPage() {
    this.nextPage.emit();
  }

  onAddToList(contentId: string) {
    this.addToList.emit(contentId);
  }

  onMoreInfo(contentId: string) {
    // Find the content item to determine its type
    const contentItem = this.content.find(item => item.id === contentId);
    
    if (!contentItem) {
      // Fallback to emitting the event if content not found
      this.moreInfo.emit(contentId);
      return;
    }

    // For episodes, always emit the event (let parent component handle)
    if (this.isEpisode(contentItem)) {
      this.moreInfo.emit(contentId);
      return;
    }

    // For content items, route based on content type
    const content = this.asContent(contentItem);
    const contentTypeName = content.contentType.name.toLowerCase();
    
    if (contentTypeName === 'movie') {
      this.router.navigate(['/content', contentId]);
    } else if (contentTypeName === 'tv show' || contentTypeName === 'series' || contentTypeName === 'tv series') {
      this.router.navigate(['/show', contentId]);
    } else {
      // Default fallback - emit event for unknown content types
      this.moreInfo.emit(contentId);
    }
  }

  onPlayContent(contentId: string) {
    this.playContent.emit(contentId);
  }

  isEpisode(item: Content | Episode): item is Episode {
    return 'seasonNumber' in item && 'episodeNumber' in item;
  }

  asEpisode(item: Content | Episode): Episode {
    return item as Episode;
  }

  asContent(item: Content | Episode): Content {
    return item as Content;
  }
}
