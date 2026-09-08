import { ComponentFixture, TestBed } from '@angular/core/testing';
import { AuthorPage } from './author-page.component';

describe('AuthorPage', () => {
  let component: AuthorPage;
  let fixture: ComponentFixture<AuthorPage>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [AuthorPage],
    }).compileComponents();

    fixture = TestBed.createComponent(AuthorPage);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
