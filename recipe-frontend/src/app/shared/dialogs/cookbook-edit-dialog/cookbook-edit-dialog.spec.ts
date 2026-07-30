import { ComponentFixture, TestBed } from '@angular/core/testing';

import { CookbookEditDialog } from './edit-cookbook-dialog';

describe('CookbookEditDialog', () => {
  let component: CookbookEditDialog;
  let fixture: ComponentFixture<CookbookEditDialog>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [CookbookEditDialog],
    }).compileComponents();

    fixture = TestBed.createComponent(CookbookEditDialog);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
