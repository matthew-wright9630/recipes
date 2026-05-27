import { Routes } from '@angular/router';
import { RecipeListComponent } from './features/recipe/recipe-list/recipe-list.component';
import { RecipeDetail } from './features/recipe/recipe-detail/recipe-detail';

export const routes: Routes = [
    {
        path: 'homepage',
        component: RecipeListComponent,
    },
    {
        path: 'recipe/:id', component: RecipeDetail,
    }
];
