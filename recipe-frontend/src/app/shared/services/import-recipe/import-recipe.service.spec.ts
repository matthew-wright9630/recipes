import { TestBed } from '@angular/core/testing';

import { ImportRecipeService } from './import-recipe.service';

describe('ImportRecipeService', () => {
  let service: ImportRecipeService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(ImportRecipeService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
