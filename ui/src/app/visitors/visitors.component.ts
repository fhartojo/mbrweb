import { Component, OnInit } from '@angular/core';

@Component({
  selector: 'app-visitors',
  templateUrl: './visitors.component.html',
  styleUrls: ['./visitors.component.css']
})
export class VisitorsComponent implements OnInit {

  displayedColumns: string[] = ['timestamp', 'id', 'name', 'notes'];
  visitors: Visitor[] = VISITOR_DATA;

  constructor() { }

  ngOnInit() {
  }
}

export interface Visitor {
  timestamp: number
  , id: number
  , name: string
  , notes: string
  , ok: boolean
}

const VISITOR_DATA: Visitor[] = [
  {timestamp: 0, id: 1, name: 'Jane Doe', notes: '', ok: true}
  , {timestamp: 1000, id: 2, name: 'John Doe', notes: 'bbb', ok: true}
  , {timestamp: 2000, id: 3, name: 'Joe Doe', notes: '', ok: false}
];
