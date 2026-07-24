import { TestBed } from '@angular/core/testing';

import { CookbookStateServiceService } from './cookbook-state-service.service';

describe('CookbookStateServiceService', () => {
  let service: CookbookStateServiceService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(CookbookStateServiceService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
