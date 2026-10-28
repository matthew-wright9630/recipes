import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ImportRecipeDialog } from './import-recipe-dialog';

describe('ImportRecipeDialog', () => {
  let component: ImportRecipeDialog;
  let fixture: ComponentFixture<ImportRecipeDialog>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ImportRecipeDialog],
    }).compileComponents();

    fixture = TestBed.createComponent(ImportRecipeDialog);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
