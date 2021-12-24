import { TestBed } from '@angular/core/testing';

import { DatabaseLoggerService } from './database-logger.service';

describe('DatabaseLoggerService', () => {
  beforeEach(() => TestBed.configureTestingModule({}));

  it('should be created', () => {
    const service: DatabaseLoggerService = TestBed.get(DatabaseLoggerService);
    expect(service).toBeTruthy();
  });
});
