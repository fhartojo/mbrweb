import { TestBed } from '@angular/core/testing';

import { VisitorDataService } from './visitor-data.service';

describe('VisitorDataService', () => {
  beforeEach(() => TestBed.configureTestingModule({}));

  it('should be created', () => {
    const service: VisitorDataService = TestBed.get(VisitorDataService);
    expect(service).toBeTruthy();
  });
});
