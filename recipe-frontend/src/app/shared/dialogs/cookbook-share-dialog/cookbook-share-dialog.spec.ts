import { ComponentFixture, TestBed } from '@angular/core/testing';

import { CookbookShareDialog } from '../recipe-share-dialog/recipe-share-dialog';

describe('CookbookShareDialog', () => {
  let component: CookbookShareDialog;
  let fixture: ComponentFixture<CookbookShareDialog>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [CookbookShareDialog],
    }).compileComponents();

    fixture = TestBed.createComponent(CookbookShareDialog);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
