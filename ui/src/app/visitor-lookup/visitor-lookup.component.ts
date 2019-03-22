import { Component, OnInit, AfterViewInit, ViewChild, ElementRef } from '@angular/core';
import { VisitorDataService } from '../visitor-data.service';

@Component({
  selector: 'app-visitor-lookup',
  templateUrl: './visitor-lookup.component.html',
  styleUrls: ['./visitor-lookup.component.css']
})
export class VisitorLookupComponent implements OnInit, AfterViewInit {

  @ViewChild('idLookup') idLookupElement: ElementRef;

  constructor(private visitorDataService: VisitorDataService) { }

  ngOnInit() {
    console.log(`VisitorLookupComponent:  ngOnInit()`);
  }

  ngAfterViewInit() {
    setTimeout(() => {
      this.idLookupElement.nativeElement.focus();
    }, 0);
  }

  public getVisitor(id: string) {
    this.visitorDataService.getVisitor(id).subscribe(visitor => this.visitorDataService.addVisitor(visitor));
  }
}
