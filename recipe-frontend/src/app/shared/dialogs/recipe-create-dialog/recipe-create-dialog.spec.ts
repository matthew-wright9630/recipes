import { ComponentFixture, TestBed } from '@angular/core/testing';

import { RecipeCreateDialog } from './recipe-create-dialog';

describe('RecipeCreateDialog', () => {
  let component: RecipeCreateDialog;
  let fixture: ComponentFixture<RecipeCreateDialog>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [RecipeCreateDialog],
    }).compileComponents();

    fixture = TestBed.createComponent(RecipeCreateDialog);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
