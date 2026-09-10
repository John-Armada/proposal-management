import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ProposalListPage } from './proposal-list-page.component';

describe('ProposalListPage', () => {
  let component: ProposalListPage;
  let fixture: ComponentFixture<ProposalListPage>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ProposalListPage],
    }).compileComponents();

    fixture = TestBed.createComponent(ProposalListPage);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
