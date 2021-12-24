import { Component, OnInit } from '@angular/core';
import { MatTableDataSource } from '@angular/material/table';
import { DatabaseLoggerService } from '../database-logger.service';
import { LogEntry } from '../log-entry';

@Component({
  selector: 'app-maintenance-log',
  templateUrl: './maintenance-log.component.html',
  styleUrls: ['./maintenance-log.component.css']
})
export class MaintenanceLogComponent implements OnInit {

  private logEntries: LogEntry[] = [];
  public dataSource = new MatTableDataSource<LogEntry>();
  public displayedColumns: string[] = ['timestamp', 'message', 'exception'];

  constructor(private dbLoggerService: DatabaseLoggerService) { }

  ngOnInit() {
    this.getLogEntries();
  }

  public getLogEntries(): void {
    this.dbLoggerService.getLogEntries().subscribe({
      next: response => this.logEntries = response
      , complete: () => this.dataSource.data = [...this.logEntries]
    });
  }
}
