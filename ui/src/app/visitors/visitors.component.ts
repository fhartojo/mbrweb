import { Component, OnInit, OnDestroy } from '@angular/core';
import { VisitorDataService } from '../visitor-data.service';
import { Visitor } from '../visitor';

@Component({
  selector: 'app-visitors',
  templateUrl: './visitors.component.html',
  styleUrls: ['./visitors.component.css']
})
export class VisitorsComponent implements OnInit {

  displayedColumns: string[] = ['timestamp', 'memberId', 'lastName', 'firstName', 'notes'];
  visitors: Visitor[];

  constructor(private visitorDataService: VisitorDataService) {
    console.log(`VisitorsComponent:  constructor()`);
  }

  ngOnInit() {
    console.log(`VisitorsComponent:  ngOnInit()`);
    this.visitorDataService.visitors$.subscribe(visitors => this.visitors = visitors);
    this.getVisitors();
  }

  ngOnDestroy() {
    console.log(`VisitorsComponent:  ngOnDestroy`);
  }

  getVisitors(): void {
    this.visitorDataService.getVisitors().subscribe(visitors => this.visitorDataService.initVisitors(visitors));
  }
}
