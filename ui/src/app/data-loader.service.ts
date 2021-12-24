import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { DataLoadStatus } from './data-load-status';

@Injectable({
  providedIn: 'root'
})
export class DataLoaderService {

  private loadMembershipDataEndpoint: string = `api/loadMembershipData`;

  constructor(private http: HttpClient) { }

  public loadMembershipData(): Observable<DataLoadStatus> {
    return this.http.get<DataLoadStatus>(`${this.loadMembershipDataEndpoint}`);
  }
}
