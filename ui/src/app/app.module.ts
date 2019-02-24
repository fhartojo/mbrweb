import { BrowserModule } from '@angular/platform-browser';
import { NgModule } from '@angular/core';

import { AppComponent } from './app.component';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { MatTabsModule } from '@angular/material/tabs';
import { MatInputModule } from '@angular/material/input';
import { VisitorLookupComponent } from './visitor-lookup/visitor-lookup.component';
import { VisitorsComponent } from './visitors/visitors.component';

@NgModule({
  declarations: [
    AppComponent,
    VisitorLookupComponent,
    VisitorsComponent
  ],
  imports: [
    BrowserModule,
    BrowserAnimationsModule,
    MatTabsModule,
    MatInputModule
  ],
  providers: [],
  bootstrap: [AppComponent]
})
export class AppModule { }
