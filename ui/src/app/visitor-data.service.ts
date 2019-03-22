import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable, BehaviorSubject } from 'rxjs';
import { Visitor } from './visitor';

@Injectable({
  providedIn: 'root'
})
export class VisitorDataService {

  getVisitorApiUrl: string = `api/visitor`;
  getVisitorsApiUrl: string = `api/visitors`;

  private visitors: Visitor[] = [];
  private visitorsSubject = new BehaviorSubject<Visitor[]>(this.visitors);
  visitors$ = this.visitorsSubject.asObservable();

  constructor(private http: HttpClient) { }

  public getVisitors(): Observable<Visitor[]> {
    return this.http.get<Visitor[]>(`${this.getVisitorsApiUrl}`);
  }

  public getVisitor(id: string): Observable<Visitor> {
    return this.http.get<Visitor>(`${this.getVisitorApiUrl}/${id}`);
  }

  public initVisitors(visitors: Visitor[]): void {
    this.visitors = visitors;
    this.visitorsSubject.next(this.visitors);
  }

  public addVisitor(visitor: Visitor): void {
    this.visitors.unshift(visitor);
    this.visitorsSubject.next(this.visitors);
  }
}
