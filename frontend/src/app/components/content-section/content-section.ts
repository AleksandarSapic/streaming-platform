import {Component, EventEmitter, Input, Output} from '@angular/core';
import {MatButtonModule} from '@angular/material/button';
import {MatIconModule} from '@angular/material/icon';
import {MatMenuModule} from '@angular/material/menu';
import {MatCardModule} from '@angular/material/card';
import {CommonModule} from '@angular/common';
import {Content} from '../../interfaces/content.interface';

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
  @Input() content: Content[] = [];
  @Input() isLoading: boolean = false;
  @Input() currentPage: number = 0;
  @Input() totalPages: number = 0;
  @Input() isWatchlist: boolean = false;

  @Output() previousPage = new EventEmitter<void>();
  @Output() nextPage = new EventEmitter<void>();
  @Output() addToList = new EventEmitter<string>();
  @Output() moreInfo = new EventEmitter<string>();
  @Output() playContent = new EventEmitter<string>();

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
    this.moreInfo.emit(contentId);
  }

  onPlayContent(contentId: string) {
    this.playContent.emit(contentId);
  }
}
