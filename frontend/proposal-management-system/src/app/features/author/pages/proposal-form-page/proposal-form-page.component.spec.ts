import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ProposalFormPage } from './proposal-form-page.component';

describe('ProposalFormPage', () => {
  let component: ProposalFormPage;
  let fixture: ComponentFixture<ProposalFormPage>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ProposalFormPage],
    }).compileComponents();

    fixture = TestBed.createComponent(ProposalFormPage);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
