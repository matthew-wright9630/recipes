import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ViewedRecipes } from './viewed-recipes';

describe('ViewedRecipes', () => {
  let component: ViewedRecipes;
  let fixture: ComponentFixture<ViewedRecipes>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ViewedRecipes],
    }).compileComponents();

    fixture = TestBed.createComponent(ViewedRecipes);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
