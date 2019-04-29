import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { LogEntry } from './log-entry';

@Injectable({
  providedIn: 'root'
})
export class DatabaseLoggerService {

  private logApiUrl: string = `api/log`;

  constructor(private http: HttpClient) { }

  public getLogEntries(): Observable<LogEntry[]> {
    return this.http.get<LogEntry[]>(`${this.logApiUrl}`);
  }
}
