import { Routes } from '@angular/router';
import { RecipeListComponent } from './features/recipe/recipe-list/recipe-list.component';
import { RecipeDetail } from './features/recipe/recipe-detail/recipe-detail';
import { Profile } from './features/profile/profile-component/profile';
import { authGuard } from './auth-guard';
import { UserRecipe } from './features/recipe/user-recipe/user-recipe';
import { RecipeEdit } from './features/recipe/recipe-edit/recipe-edit';

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
    path: 'recipe/:id/edit',
    component: RecipeEdit,
    canActivate: [authGuard],
  },
];
