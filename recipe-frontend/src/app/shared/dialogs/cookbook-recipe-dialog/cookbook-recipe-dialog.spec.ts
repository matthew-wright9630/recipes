import { ComponentFixture, TestBed } from '@angular/core/testing';

import { CookbookRecipeDialog } from './cookbook-recipe-dialog';

describe('CookbookRecipeDialog', () => {
  let component: CookbookRecipeDialog;
  let fixture: ComponentFixture<CookbookRecipeDialog>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [CookbookRecipeDialog],
    }).compileComponents();

    fixture = TestBed.createComponent(CookbookRecipeDialog);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
