import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ProposalFilterBar } from './proposal-filter-bar.component';

describe('ProposalFilterBar', () => {
  let component: ProposalFilterBar;
  let fixture: ComponentFixture<ProposalFilterBar>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ProposalFilterBar],
    }).compileComponents();

    fixture = TestBed.createComponent(ProposalFilterBar);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
