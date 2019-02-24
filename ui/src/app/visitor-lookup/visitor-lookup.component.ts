import { Component, OnInit, AfterViewInit, ViewChild, ElementRef } from '@angular/core';

@Component({
  selector: 'app-visitor-lookup',
  templateUrl: './visitor-lookup.component.html',
  styleUrls: ['./visitor-lookup.component.css']
})
export class VisitorLookupComponent implements OnInit, AfterViewInit {

  @ViewChild('idLookup') idLookupElement: ElementRef;

  constructor() { }

  ngOnInit() {
  }

  ngAfterViewInit() {
    setTimeout(() => {
      this.idLookupElement.nativeElement.focus();
    }, 0);
  }
}
