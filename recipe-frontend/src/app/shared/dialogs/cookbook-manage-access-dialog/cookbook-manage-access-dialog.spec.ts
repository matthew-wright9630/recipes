import { ComponentFixture, TestBed } from '@angular/core/testing';

import { CookbookManageAccessDialog } from './cookbook-manage-access-dialog';

describe('CookbookManageAccessDialog', () => {
  let component: CookbookManageAccessDialog;
  let fixture: ComponentFixture<CookbookManageAccessDialog>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [CookbookManageAccessDialog],
    }).compileComponents();

    fixture = TestBed.createComponent(CookbookManageAccessDialog);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
