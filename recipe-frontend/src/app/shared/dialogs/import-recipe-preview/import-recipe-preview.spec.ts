import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ImportRecipePreview } from './import-recipe-preview';

describe('ImportRecipePreview', () => {
  let component: ImportRecipePreview;
  let fixture: ComponentFixture<ImportRecipePreview>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ImportRecipePreview],
    }).compileComponents();

    fixture = TestBed.createComponent(ImportRecipePreview);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
