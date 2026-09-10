import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ProposalTable } from './proposal-table.component';

describe('ProposalTable', () => {
  let component: ProposalTable;
  let fixture: ComponentFixture<ProposalTable>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ProposalTable],
    }).compileComponents();

    fixture = TestBed.createComponent(ProposalTable);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
