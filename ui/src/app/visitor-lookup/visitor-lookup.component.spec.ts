import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { VisitorLookupComponent } from './visitor-lookup.component';

describe('VisitorLookupComponent', () => {
  let component: VisitorLookupComponent;
  let fixture: ComponentFixture<VisitorLookupComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ VisitorLookupComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(VisitorLookupComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
