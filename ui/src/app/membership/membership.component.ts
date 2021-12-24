import { Component, OnInit } from '@angular/core';
import { DataLoaderService } from '../data-loader.service';
import { DataLoadStatus } from '../data-load-status';
import { MatDialog, MatDialogRef } from '@angular/material/dialog';
import { MatSnackBar } from '@angular/material/snack-bar';
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
    , private snackBar: MatSnackBar
  ) { }

  ngOnInit() {
  }

  public loadMembershipData(): void {
    let dataLoadStatus: DataLoadStatus = new DataLoadStatus();

    dataLoadStatus.ok = false;
    dataLoadStatus.message = "";

    let spinnerDialogRef: MatDialogRef<DataLoadSpinner> = this.dialog.open(DataLoadSpinner, {
      panelClass: 'transparent-spinner'
      , disableClose: true
    });

    let subscription = this.dataLoaderService.loadMembershipData().subscribe(
      response => {
        dataLoadStatus = response;
        subscription.unsubscribe();
        spinnerDialogRef.close();
        this.showDataLoadStatus(dataLoadStatus);
      }
      , (error) => {
        subscription.unsubscribe();
        spinnerDialogRef.close();
        this.showDataLoadStatus(dataLoadStatus);
      }
    );
  }

  private showDataLoadStatus(dataLoadStatus: DataLoadStatus): void {
    let message: string;

    if (dataLoadStatus.ok) {
      message = "Load successful.";
    } else {
      message = "Failed to load.  Check the Maintenance Log tab for details.";
    }

    this.snackBar.open(message, "Close", {
      duration: 10000
    });
  }
}

@Component({
  selector: 'data-load-spinner',
  templateUrl: 'data-load-spinner.html',
  styleUrls: ['./data-load-spinner.css']
})
export class DataLoadSpinner {
}
