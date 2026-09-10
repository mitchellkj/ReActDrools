import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { OptionsResponse, PricingRequest, PricingResponse } from './models';

@Injectable({
  providedIn: 'root'
})
export class PricingService {
  private baseUrl = '/api';

  constructor(private http: HttpClient) {}

  getOptions(): Observable<OptionsResponse> {
    return this.http.get<OptionsResponse>(`${this.baseUrl}/options`);
  }

  computePricing(request: PricingRequest): Observable<PricingResponse> {
    return this.http.post<PricingResponse>(`${this.baseUrl}/pricing`, request, {
      withCredentials: true
    });
  }

  checkHealth(): Observable<any> {
    return this.http.get<any>('/health');
  }
}
