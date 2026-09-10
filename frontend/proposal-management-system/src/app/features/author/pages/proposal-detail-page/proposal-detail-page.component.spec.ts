import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ProposalDetailPage } from './proposal-detail-page.component';

describe('ProposalDetailPage', () => {
  let component: ProposalDetailPage;
  let fixture: ComponentFixture<ProposalDetailPage>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ProposalDetailPage],
    }).compileComponents();

    fixture = TestBed.createComponent(ProposalDetailPage);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
