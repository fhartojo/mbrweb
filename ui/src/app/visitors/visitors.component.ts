import { Component, OnInit, OnDestroy, AfterViewInit, ViewChild, ElementRef } from '@angular/core';
import { VisitorDataService } from '../visitor-data.service';
import { Visitor } from '../visitor';
import { FormGroup, FormControl } from '@angular/forms';
import { MatTableDataSource } from '@angular/material/table';

@Component({
  selector: 'app-visitors',
  templateUrl: './visitors.component.html',
  styleUrls: ['./visitors.component.css']
})
export class VisitorsComponent implements OnInit, OnDestroy, AfterViewInit {

  @ViewChild('idLookup', { static: true }) idLookupElement: ElementRef;
  idForm = new FormGroup({
    id: new FormControl('')
  });
  private visitors: Visitor[];
  displayedColumns: string[] = ['timestamp', 'memberId', 'lastName', 'firstName', 'notes'];
  dataSource = new MatTableDataSource<Visitor>();

  constructor(private visitorDataService: VisitorDataService) {
    console.log(`VisitorsComponent:  constructor()`);
  }

  ngOnInit() {
    console.log(`VisitorsComponent:  ngOnInit()`);
    this.getVisitors();
  }

  ngAfterViewInit() {
    setTimeout(() => {
      this.idLookupElement.nativeElement.focus();
    }, 0);
  }

  ngOnDestroy() {
    console.log(`VisitorsComponent:  ngOnDestroy`);
  }

  getVisitor() {
    let id: string = this.idForm.get("id").value.trim();

    if (id !== "") {
      this.visitorDataService.getVisitor(id).subscribe({
        next: visitor => this.visitors.splice(0, 0, visitor)
        , complete: () => this.dataSource.data = this.visitors
      });
    }

    this.idForm.get("id").setValue("");
  }

  getVisitors() {
    this.visitorDataService.getVisitors().subscribe({
      next: visitors => this.visitors = visitors
      , complete: () => this.dataSource.data = this.visitors
    });
  }
}
