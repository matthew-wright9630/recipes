import { TestBed } from '@angular/core/testing';

import { AuthPromptService } from './auth-prompt.service';

describe('AuthPromptService', () => {
  let service: AuthPromptService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(AuthPromptService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
