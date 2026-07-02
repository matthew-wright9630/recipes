import { TestBed } from '@angular/core/testing';

import { RecipeLikeService } from './recipe-like.service';

describe('RecipeLikeService', () => {
  let service: RecipeLikeService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(RecipeLikeService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
