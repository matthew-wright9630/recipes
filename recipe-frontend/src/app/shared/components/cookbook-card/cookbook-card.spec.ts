import { ComponentFixture, TestBed } from '@angular/core/testing';

import { CookbookCard } from './cookbook-card';

describe('CookbookCard', () => {
  let component: CookbookCard;
  let fixture: ComponentFixture<CookbookCard>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [CookbookCard],
    }).compileComponents();

    fixture = TestBed.createComponent(CookbookCard);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
