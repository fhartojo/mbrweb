import { BrowserModule } from '@angular/platform-browser';
import { NgModule } from '@angular/core';

import { AppComponent } from './app.component';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { MatTabsModule } from '@angular/material/tabs';
import { MatInputModule } from '@angular/material/input';
import { MatTableModule } from '@angular/material/table';
import { MatButtonModule } from '@angular/material/button';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatIconModule } from '@angular/material/icon';
import { MatDialogModule } from '@angular/material/dialog';
import { MatSnackBarModule } from '@angular/material/snack-bar';
import { VisitorsComponent } from './visitors/visitors.component';
import { HttpClientModule } from '@angular/common/http';
import { ReactiveFormsModule } from '@angular/forms';
import { MembershipComponent, DataLoadSpinner } from './membership/membership.component';
import { MaintenanceLogComponent } from './maintenance-log/maintenance-log.component';

@NgModule({
  declarations: [
    AppComponent,
    VisitorsComponent,
    MembershipComponent,
    DataLoadSpinner,
    MaintenanceLogComponent
  ],
  imports: [
    BrowserModule,
    BrowserAnimationsModule,
    HttpClientModule,
    MatTabsModule,
    MatInputModule,
    MatTableModule,
    MatButtonModule,
    MatProgressSpinnerModule,
    MatIconModule,
    MatDialogModule,
    MatSnackBarModule,
    ReactiveFormsModule
  ],
  entryComponents: [
    DataLoadSpinner
  ],
  providers: [],
  bootstrap: [AppComponent]
})
export class AppModule { }
