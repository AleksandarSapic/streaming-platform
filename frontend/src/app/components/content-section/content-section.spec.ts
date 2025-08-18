import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { RouterTestingModule } from '@angular/router/testing';

import { ContentSection } from './content-section';

describe('ContentSection', () => {
  let component: ContentSection;
  let fixture: ComponentFixture<ContentSection>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ContentSection, HttpClientTestingModule, RouterTestingModule]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ContentSection);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});