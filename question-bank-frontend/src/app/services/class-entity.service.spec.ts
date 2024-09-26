import { TestBed } from '@angular/core/testing';

import { ClassEntityService } from './class-entity.service';

describe('ClassEntityService', () => {
  let service: ClassEntityService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(ClassEntityService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
