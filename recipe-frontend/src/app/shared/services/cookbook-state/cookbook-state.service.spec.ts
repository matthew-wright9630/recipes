import { TestBed } from '@angular/core/testing';

import { CookbookStateService } from './cookbook-state.service';

describe('CookbookStateService', () => {
  let service: CookbookStateService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(CookbookStateService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
