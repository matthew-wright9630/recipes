import { ComponentFixture, TestBed } from "@angular/core/testing";

import { RecipePreviewDialog } from "./recipe-preview-dialog";

describe("RecipePreviewDialog", () => {
  let component: RecipePreviewDialog;
  let fixture: ComponentFixture<RecipePreviewDialog>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [RecipePreviewDialog],
    }).compileComponents();

    fixture = TestBed.createComponent(RecipePreviewDialog);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it("should create", () => {
    expect(component).toBeTruthy();
  });
});
