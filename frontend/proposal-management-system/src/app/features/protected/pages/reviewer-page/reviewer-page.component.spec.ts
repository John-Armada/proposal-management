import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ReviewerPage } from './reviewer-page.component';

describe('ReviewerPage', () => {
  let component: ReviewerPage;
  let fixture: ComponentFixture<ReviewerPage>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ReviewerPage],
    }).compileComponents();

    fixture = TestBed.createComponent(ReviewerPage);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
