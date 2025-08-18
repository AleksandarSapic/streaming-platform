import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of } from 'rxjs';

import { ShowDetail } from './show-detail';

describe('ShowDetail', () => {
  let component: ShowDetail;
  let fixture: ComponentFixture<ShowDetail>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ShowDetail, HttpClientTestingModule, RouterTestingModule],
      providers: [
        {
          provide: ActivatedRoute,
          useValue: {
            params: of({ id: 'test-id' }),
            snapshot: { params: { id: 'test-id' } }
          }
        }
      ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ShowDetail);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});