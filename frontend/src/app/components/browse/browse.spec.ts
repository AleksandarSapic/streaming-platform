import {ComponentFixture, TestBed} from '@angular/core/testing';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { RouterTestingModule } from '@angular/router/testing';

import {Browse} from './browse';

describe('Browse', () => {
  let component: Browse;
  let fixture: ComponentFixture<Browse>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [Browse, HttpClientTestingModule, RouterTestingModule]
    })
      .compileComponents();

    fixture = TestBed.createComponent(Browse);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
