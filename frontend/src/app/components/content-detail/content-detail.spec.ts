import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of } from 'rxjs';

import { ContentDetail } from './content-detail';

describe('ContentDetail', () => {
  let component: ContentDetail;
  let fixture: ComponentFixture<ContentDetail>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ContentDetail, HttpClientTestingModule, RouterTestingModule],
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

    fixture = TestBed.createComponent(ContentDetail);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
