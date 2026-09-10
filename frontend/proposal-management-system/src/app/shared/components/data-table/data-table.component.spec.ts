import { ComponentFixture, TestBed } from '@angular/core/testing';
import { DataTable } from './data-table.component';

type TestRow = Record<string, unknown>;

describe('DataTable', () => {
  let component: DataTable<TestRow>;
  let fixture: ComponentFixture<DataTable<TestRow>>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [DataTable],
    }).compileComponents();

    fixture = TestBed.createComponent(DataTable<TestRow>);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
