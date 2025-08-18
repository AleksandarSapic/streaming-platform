import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { RouterTestingModule } from '@angular/router/testing';

import { AccountProfile } from './account-profile';

describe('AccountProfile', () => {
  let component: AccountProfile;
  let fixture: ComponentFixture<AccountProfile>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [AccountProfile, HttpClientTestingModule, RouterTestingModule]
    })
    .compileComponents();

    fixture = TestBed.createComponent(AccountProfile);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
