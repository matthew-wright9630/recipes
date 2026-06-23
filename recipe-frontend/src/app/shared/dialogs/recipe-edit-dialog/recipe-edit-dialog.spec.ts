import { ComponentFixture, TestBed } from '@angular/core/testing';

import { RecipeEditDialog } from './recipe-edit-dialog';

describe('RecipeEditDialog', () => {
  let component: RecipeEditDialog;
  let fixture: ComponentFixture<RecipeEditDialog>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [RecipeEditDialog],
    }).compileComponents();

    fixture = TestBed.createComponent(RecipeEditDialog);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
