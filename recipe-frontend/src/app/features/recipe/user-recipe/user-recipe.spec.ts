import { ComponentFixture, TestBed } from '@angular/core/testing';

import { UserRecipe } from './user-recipe';

describe('UserRecipe', () => {
  let component: UserRecipe;
  let fixture: ComponentFixture<UserRecipe>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [UserRecipe],
    }).compileComponents();

    fixture = TestBed.createComponent(UserRecipe);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
