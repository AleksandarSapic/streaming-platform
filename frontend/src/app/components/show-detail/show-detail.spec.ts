import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ShowDetail } from './show-detail';

describe('ShowDetail', () => {
  let component: ShowDetail;
  let fixture: ComponentFixture<ShowDetail>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ShowDetail]
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