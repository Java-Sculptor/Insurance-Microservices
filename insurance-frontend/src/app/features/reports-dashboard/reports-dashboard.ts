import { CommonModule } from '@angular/common';
import { Component, DestroyRef, computed, inject, OnInit, signal } from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { HttpErrorResponse } from '@angular/common/http';
import {
  DashboardCharts,
  DashboardSummary,
  ReportFilters,
  ReportRow,
  ReportType,
  ReportsDashboardApi
} from './reports-dashboard-api';

interface DataSegment {
  label: string;
  count: number;
  percent: number;
  color: string;
}

interface ReportOption {
  id: ReportType;
  label: string;
  icon: string;
}

const GRAPH_COLORS = ['#1ba98f', '#4d8fcd', '#f0a643', '#9174d5', '#ef7773', '#879bb7'];

@Component({
  selector: 'app-reports-dashboard',
  imports: [CommonModule],
  templateUrl: './reports-dashboard.html',
  styleUrl: './reports-dashboard.css'
})
export class ReportsDashboard implements OnInit {
  private readonly api = inject(ReportsDashboardApi);
  private readonly destroyRef = inject(DestroyRef);

  readonly view = signal<'overview' | 'reports'>('overview');
  readonly summary = signal<DashboardSummary | null>(null);
  readonly charts = signal<DashboardCharts | null>(null);
  readonly overviewLoading = signal(true);
  readonly overviewError = signal('');
  readonly reportLoading = signal(true);
  readonly reportError = signal('');
  readonly exportLoading = signal(false);
  readonly exportingError = signal('');
  readonly reportType = signal<ReportType>('policies');
  readonly rows = signal<ReportRow[]>([]);
  readonly page = signal(0);
  readonly totalPages = signal(0);
  readonly totalElements = signal(0);
  readonly appliedFilters = signal<ReportFilters>({});

  // Draft filters are edited in the form; Apply is what calls the backend.
  insuranceType = '';
  status = '';
  startDate = '';
  endDate = '';
  agentId = '';
  customerId = '';

  readonly reportOptions: readonly ReportOption[] = [
    { id: 'policies', label: 'Policies', icon: 'file-earmark-check' },
    { id: 'agents', label: 'Agents', icon: 'people' },
    { id: 'claims', label: 'Claims', icon: 'clipboard2-check' },
    { id: 'premiums', label: 'Premiums', icon: 'wallet2' },
    { id: 'renewals', label: 'Renewals', icon: 'arrow-repeat' }
  ];

  readonly reportLabel = computed(() => this.reportOptions.find(opt => opt.id === this.reportType())?.label ?? 'Reports');
  readonly columnKeys = computed(() => {
    const rows = this.rows();
    if (!rows.length) return [];
    const available = new Set<string>();
    rows.forEach(row => Object.keys(row).forEach(key => available.add(key)));
    const priority: Record<ReportType, string[]> = {
      policies: ['policyId', 'policyNumber', 'insuranceType', 'policyStatus', 'coverageAmount', 'premiumAmount', 'issueDate', 'expiryDate'],
      agents: ['agentId', 'agentCode', 'agentName', 'email', 'totalPoliciesSold', 'commissionEarned', 'joiningDate'],
      claims: ['claimId', 'policyId', 'customerId', 'claimStatus', 'claimAmount', 'settlementAmount', 'claimDate'],
      premiums: ['scheduleId', 'policyId', 'customerId', 'premiumStatus', 'premiumAmount', 'paidAmount', 'dueDate'],
      renewals: ['renewalId', 'policyId', 'renewalStatus', 'renewalDate', 'newPremiumAmount']
    };
    return [...priority[this.reportType()].filter(key => available.delete(key)), ...Array.from(available)];
  });

  readonly policySegments = computed(() => this.segments(this.charts()?.policiesByType ?? {}));
  readonly claimSegments = computed(() => this.segments(this.charts()?.claimsByStatus ?? {}));
  readonly donutBackground = computed(() => {
    const segments = this.policySegments();
    if (!segments.length) return '#e8eef4';
    let point = 0;
    const stops = segments.map(part => {
      const begin = point;
      point += part.percent;
      return `${part.color} ${begin}% ${point}%`;
    });
    return `conic-gradient(${stops.join(', ')})`;
  });
  readonly monthlyPremiums = computed(() => this.months(this.charts()?.premiumCollectedByMonth ?? {}));
  readonly monthlyPolicies = computed(() => this.months(this.charts()?.policiesIssuedByMonth ?? {}));

  ngOnInit(): void {
    this.loadOverview();
    this.loadReport();
  }

  show(view: 'overview' | 'reports'): void {
    this.view.set(view);
  }

  loadOverview(): void {
    this.overviewLoading.set(true);
    this.overviewError.set('');
    // Both endpoints are independent so a chart failure does not discard the KPI summary.
    let pending = 2;
    const complete = () => {
      pending--;
      if (!pending) this.overviewLoading.set(false);
    };
    this.api.getSummary().pipe(takeUntilDestroyed(this.destroyRef)).subscribe({
      next: data => { this.summary.set(data); complete(); },
      error: error => { this.overviewError.set(this.errorMessage(error)); complete(); }
    });
    this.api.getCharts().pipe(takeUntilDestroyed(this.destroyRef)).subscribe({
      next: data => { this.charts.set(data); complete(); },
      error: error => { this.overviewError.set(this.errorMessage(error)); complete(); }
    });
  }

  selectReport(type: ReportType): void {
    this.reportType.set(type);
    this.page.set(0);
    this.rows.set([]);
    this.loadReport();
  }

  applyFilters(event: Event): void {
    event.preventDefault();
    if (this.startDate && this.endDate && this.startDate > this.endDate) {
      this.reportError.set('Start date cannot be after end date.');
      return;
    }
    this.appliedFilters.set({
      insuranceType: this.insuranceType,
      status: this.status.trim(),
      startDate: this.startDate,
      endDate: this.endDate,
      agentId: this.agentId.trim(),
      customerId: this.customerId.trim()
    });
    this.page.set(0);
    this.loadReport();
  }

  resetFilters(): void {
    this.insuranceType = '';
    this.status = '';
    this.startDate = '';
    this.endDate = '';
    this.agentId = '';
    this.customerId = '';
    this.appliedFilters.set({});
    this.page.set(0);
    this.loadReport();
  }

  changePage(next: number): void {
    if (next < 0 || next >= this.totalPages() || this.reportLoading()) return;
    this.page.set(next);
    this.loadReport();
  }

  loadReport(): void {
    this.reportLoading.set(true);
    this.reportError.set('');
    this.api.getReport(this.reportType(), this.appliedFilters(), this.page())
      .pipe(takeUntilDestroyed(this.destroyRef)).subscribe({
        next: result => {
          this.rows.set(result.content ?? []);
          this.totalPages.set(result.totalPages ?? 0);
          this.totalElements.set(result.totalElements ?? 0);
          this.reportLoading.set(false);
        },
        error: error => {
          this.rows.set([]);
          this.totalPages.set(0);
          this.totalElements.set(0);
          this.reportError.set(this.errorMessage(error));
          this.reportLoading.set(false);
        }
      });
  }

  exportCsv(): void {
    if (this.exportLoading()) return;
    this.exportLoading.set(true);
    this.exportingError.set('');
    const type = this.reportType();
    this.api.downloadCsv(type, this.appliedFilters()).pipe(takeUntilDestroyed(this.destroyRef)).subscribe({
      next: blob => {
        const url = URL.createObjectURL(blob);
        const link = document.createElement('a');
        link.href = url;
        link.download = `${type}-report.csv`;
        document.body.append(link);
        link.click();
        link.remove();
        setTimeout(() => URL.revokeObjectURL(url), 1000);
        this.exportLoading.set(false);
      },
      error: error => {
        this.exportingError.set(this.errorMessage(error));
        this.exportLoading.set(false);
      }
    });
  }

  formatNumber(value: number | undefined | null): string {
    return Number(value ?? 0).toLocaleString('en-IN');
  }

  formatCurrency(value: number | undefined | null): string {
    return new Intl.NumberFormat('en-IN', {
      style: 'currency', currency: 'INR', maximumFractionDigits: 0
    }).format(Number(value ?? 0));
  }

  label(key: string): string {
    return key.replace(/([a-z0-9])([A-Z])/g, '$1 $2').replace(/_/g, ' ').replace(/^./, c => c.toUpperCase());
  }

  cell(row: ReportRow, key: string): string {
    const value = row[key];
    if (value === null || value === undefined || value === '') return '—';
    if (typeof value === 'object') return JSON.stringify(value);
    return String(value);
  }

  barWidth(value: number, values: Array<{ value: number }>): number {
    const max = Math.max(0, ...values.map(v => v.value));
    return max === 0 ? 0 : Math.max(2, value / max * 100);
  }

  private segments(data: Record<string, number>): DataSegment[] {
    const entries = Object.entries(data).filter(([, value]) => Number(value) > 0);
    const total = entries.reduce((sum, [, value]) => sum + Number(value), 0);
    return entries.map(([label, value], index) => ({
      label: this.label(label), count: Number(value),
      percent: total ? (Number(value) / total) * 100 : 0,
      color: GRAPH_COLORS[index % GRAPH_COLORS.length]
    }));
  }

  private months(data: Record<string, number>): Array<{ label: string; value: number }> {
    return Object.entries(data).sort(([a], [b]) => a.localeCompare(b)).map(([label, value]) => ({ label, value: Number(value) }));
  }

  private errorMessage(error: unknown): string {
    if (error instanceof HttpErrorResponse) {
      if (error.status === 0) return 'Reporting backend is offline. Start reports-dashboard-service on localhost:8095, then retry.';
      return `API request failed (HTTP ${error.status}). ${error.error?.message || error.message}`;
    }
    return error instanceof Error ? error.message : 'The request could not be completed.';
  }
}
