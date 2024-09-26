import { TestBed } from '@angular/core/testing';

import { MCQOptionServiceService } from './mcqoption-service.service';

describe('MCQOptionServiceService', () => {
  let service: MCQOptionServiceService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(MCQOptionServiceService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
