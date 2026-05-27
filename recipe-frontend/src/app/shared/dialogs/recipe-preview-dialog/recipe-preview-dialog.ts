import { Component, inject } from "@angular/core";
import {MAT_DIALOG_DATA, MatDialog, MatDialogActions, MatDialogContent, MatDialogModule, MatDialogTitle} from "@angular/material/dialog";
import { Recipe } from "../../models/recipe";
import { RouterLink } from "@angular/router";
import { CommonModule } from "@angular/common";

@Component({
  selector: "app-recipe-preview-dialog",
  imports: [MatDialogModule, MatDialogContent, MatDialogActions, MatDialogTitle, RouterLink, CommonModule],
  templateUrl: "./recipe-preview-dialog.html",
  styleUrl: "./recipe-preview-dialog.scss",
})
export class RecipePreviewDialog {
  recipe = inject(MAT_DIALOG_DATA);
}
