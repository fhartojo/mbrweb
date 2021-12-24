import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Visitor } from './visitor';

@Injectable({
  providedIn: 'root'
})
export class VisitorDataService {

  getVisitorApiUrl: string = `api/visitor`;
  getVisitorsApiUrl: string = `api/visitors`;

  constructor(private http: HttpClient) { }

  public getVisitors(): Observable<Visitor[]> {
    return this.http.get<Visitor[]>(`${this.getVisitorsApiUrl}`);
  }

  public getVisitor(id: string): Observable<Visitor> {
    return this.http.get<Visitor>(`${this.getVisitorApiUrl}/${id}`);
  }
}
