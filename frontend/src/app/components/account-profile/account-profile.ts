import {Component, OnInit} from '@angular/core';
import {CommonModule} from '@angular/common';
import {MatCardModule} from '@angular/material/card';
import {MatIconModule} from '@angular/material/icon';
import {MatDividerModule} from '@angular/material/divider';
import {AuthService} from '../../services/auth.service';

@Component({
  selector: 'app-account-profile',
  imports: [
    CommonModule,
    MatCardModule,
    MatIconModule,
    MatDividerModule,
  ],
  templateUrl: './account-profile.html',
  styleUrl: './account-profile.css'
})
export class AccountProfile implements OnInit {

  constructor(private authService: AuthService) {
  }

  ngOnInit(): void {
  }

  get currentUser() {
    return this.authService.currentUser
  }
}
