import { Routes } from '@angular/router';
import { RecipeListComponent } from './features/recipe/recipe-list/recipe-list.component';
import { RecipeDetail } from './features/recipe/recipe-detail/recipe-detail';
import { Profile } from './features/profile/profile-component/profile';
import { authGuard } from './auth-guard';
import { UserRecipe } from './features/recipe/user-recipe/user-recipe';
import { LikedRecipes } from './features/liked-recipes/liked-recipes';
import { ViewedRecipes } from './features/viewed-recipes/viewed-recipes';

export const routes: Routes = [
  {
    path: '',
    component: RecipeListComponent,
  },
  {
    path: 'recipe/:id',
    component: RecipeDetail,
  },
  {
    path: 'profile',
    component: Profile,
    canActivate: [authGuard],
  },
  {
    path: 'recipes',
    component: UserRecipe,
    canActivate: [authGuard],
  },
  {
    path: 'recipes/liked',
    component: LikedRecipes,
    canActivate: [authGuard],
  },
  {
    path: 'recipes/viewed',
    component: ViewedRecipes,
    canActivate: [authGuard],
  },
];
