import { Component, OnInit } from '@angular/core';
import { DataLoaderService } from '../data-loader.service';
import { DataLoadStatus } from '../data-load-status';
import { MatDialog, MatDialogRef } from '@angular/material';
import { Observable } from 'rxjs';

@Component({
  selector: 'app-membership',
  templateUrl: './membership.component.html',
  styleUrls: ['./membership.component.css']
})
export class MembershipComponent implements OnInit {

  constructor(
    private dataLoaderService: DataLoaderService
    , private dialog: MatDialog
  ) { }

  ngOnInit() {
  }

  public loadMembershipData(): void {
    let dataLoadStatus: DataLoadStatus;

    let spinnerDialogRef: MatDialogRef<DataLoadSpinner> = this.dialog.open(DataLoadSpinner, {
      panelClass: 'transparent-spinner'
      , disableClose: true
    });

    let subscription = this.dataLoaderService.loadMembershipData().subscribe(
      response => {
        dataLoadStatus = response;
        subscription.unsubscribe();
        spinnerDialogRef.close();
      }
      , (error) => {
        subscription.unsubscribe();
        spinnerDialogRef.close();
      }
    );
  }
}

@Component({
  selector: 'data-load-spinner',
  templateUrl: 'data-load-spinner.html',
  styleUrls: ['./data-load-spinner.css']
})
export class DataLoadSpinner {
}
