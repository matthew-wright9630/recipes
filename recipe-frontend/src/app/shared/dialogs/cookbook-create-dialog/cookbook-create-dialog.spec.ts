import { ComponentFixture, TestBed } from '@angular/core/testing';

import { CookbookCreateDialog } from './cookbook-create-dialog';

describe('CookbookCreateDialog', () => {
  let component: CookbookCreateDialog;
  let fixture: ComponentFixture<CookbookCreateDialog>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [CookbookCreateDialog],
    }).compileComponents();

    fixture = TestBed.createComponent(CookbookCreateDialog);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
