import { Routes } from '@angular/router';
import { RecipeComponent } from './shared/components/recipe-card/recipe-card.component'
import { RecipeListComponent } from './features/recipe/recipe-list/recipe-list.component';

export const routes: Routes = [
    {
        path: 'homepage',
        component: RecipeListComponent,
    }
];
