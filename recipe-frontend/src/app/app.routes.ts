import { Routes } from '@angular/router';
import { RecipeListComponent } from './features/recipe/recipe-list/recipe-list.component';
import { RecipeDetail } from './features/recipe/recipe-detail/recipe-detail';
import { Profile } from './features/profile/profile';
import { authGuard } from './auth-guard';

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
];
