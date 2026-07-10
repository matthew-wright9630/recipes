import { ComponentFixture, TestBed } from '@angular/core/testing';

import { LikedRecipes } from './liked-recipes';

describe('LikedRecipes', () => {
  let component: LikedRecipes;
  let fixture: ComponentFixture<LikedRecipes>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [LikedRecipes],
    }).compileComponents();

    fixture = TestBed.createComponent(LikedRecipes);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
