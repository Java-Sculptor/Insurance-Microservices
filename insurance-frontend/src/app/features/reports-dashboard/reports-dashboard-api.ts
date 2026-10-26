import { HttpClient, HttpParams } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { map, Observable } from 'rxjs';
import { environment } from '../../../environments/environment';

export type ReportType = 'policies' | 'agents' | 'claims' | 'premiums' | 'renewals';
export type ReportRow = Record<string, unknown>;

export interface ApiResponse<T> {
  success: boolean;
  message: string;
  data: T | null;
  error?: string | null;
}

export interface DashboardSummary {
  totalPolicies: number;
  activePolicies: number;
  premiumCollected: number;
  outstandingPremium: number;
  pendingClaims: number;
  settledClaims: number;
  renewalsDue: number;
  activeAgents: number;
}

export interface DashboardCharts {
  policiesByType: Record<string, number>;
  claimsByStatus: Record<string, number>;
  premiumCollectedByMonth: Record<string, number>;
  policiesIssuedByMonth: Record<string, number>;
  policiesSoldByAgent: Record<string, number>;
}

export interface PageResponse<T> {
  content: T[];
  pageNumber: number;
  pageSize: number;
  totalElements: number;
  totalPages: number;
  first: boolean;
  last: boolean;
}

export interface ReportFilters {
  insuranceType?: string;
  status?: string;
  startDate?: string;
  endDate?: string;
  agentId?: string;
  customerId?: string;
  policyId?: string;
  agentCode?: string;
}

const EXPORT_TYPES: Record<ReportType, string> = {
  policies: 'POLICY',
  agents: 'AGENT',
  claims: 'CLAIM',
  premiums: 'PREMIUM',
  renewals: 'RENEWAL'
};

@Injectable({ providedIn: 'root' })
export class ReportsDashboardApi {
  private readonly http = inject(HttpClient);
  private readonly base = environment.apiBaseUrl;

  getSummary(): Observable<DashboardSummary> {
    return this.getData<DashboardSummary>(`${this.base}/dashboard/summary`);
  }

  getCharts(): Observable<DashboardCharts> {
    return this.getData<DashboardCharts>(`${this.base}/dashboard/charts`);
  }

  getReport(type: ReportType, filters: ReportFilters, page: number, size = 10): Observable<PageResponse<ReportRow>> {
    const params = this.params(filters).set('page', String(page)).set('size', String(size));
    return this.getData<PageResponse<ReportRow>>(`${this.base}/reports/${type}`, params);
  }

  downloadCsv(type: ReportType, filters: ReportFilters): Observable<Blob> {
    const params = this.params(filters).set('type', EXPORT_TYPES[type]);
    return this.http.get(`${this.base}/reports/export/csv`, { params, responseType: 'blob' });
  }

  private getData<T>(url: string, params?: HttpParams): Observable<T> {
    return this.http.get<ApiResponse<T>>(url, { params }).pipe(
      map(result => {
        if (!result.success || result.data === null || result.data === undefined) {
          throw new Error(result.error || result.message || 'The backend returned no data.');
        }
        return result.data;
      })
    );
  }

  private params(filters: ReportFilters): HttpParams {
    let params = new HttpParams();
    for (const [name, value] of Object.entries(filters)) {
      if (value !== undefined && value !== null && String(value).trim() !== '') {
        params = params.set(name, String(value).trim());
      }
    }
    return params;
  }
}
