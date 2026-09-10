import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ProposalStatusBadge } from './proposal-status-badge.component';

describe('ProposalStatusBadge', () => {
  let component: ProposalStatusBadge;
  let fixture: ComponentFixture<ProposalStatusBadge>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ProposalStatusBadge],
    }).compileComponents();

    fixture = TestBed.createComponent(ProposalStatusBadge);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
