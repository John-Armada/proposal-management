import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ProposalReviewPanel } from './proposal-review-panel.component';

describe('ProposalReviewPanel', () => {
  let component: ProposalReviewPanel;
  let fixture: ComponentFixture<ProposalReviewPanel>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ProposalReviewPanel],
    }).compileComponents();

    fixture = TestBed.createComponent(ProposalReviewPanel);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
