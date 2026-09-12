import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { HttpClientModule } from '@angular/common/http';
import { PricingService } from './pricing.service';
import { CurrencyExchange, Product, PricingResponse, AuditItem } from './models';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [CommonModule, FormsModule, HttpClientModule],
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})
export class AppComponent implements OnInit {
  title = 'ReAct Drools Coordinator';

  products: Product[] = [];
  currencies: CurrencyExchange[] = [];

  selectedProduct: string = 'Apple MacBook Pro 14"';
  selectedCurrency: string = 'EUR';
  exchangeRate: number = 0.85;

  loading: boolean = false;
  error: string | null = null;
  result: PricingResponse | null = null;
  executionDurationMs: number = 0;

  backendHealthy: boolean = false;
  backendInfo: string = 'Connecting to Spring Boot...';

  constructor(private pricingService: PricingService) {}

  ngOnInit(): void {
    this.checkHealth();
    this.loadOptions();
  }

  checkHealth(): void {
    this.pricingService.checkHealth().subscribe({
      next: (data) => {
        this.backendHealthy = true;
        this.backendInfo = `${data.engine} (${data.stack})`;
      },
      error: () => {
        this.backendHealthy = false;
        this.backendInfo = 'Spring Boot Drools backend offline on port 8080';
      }
    });
  }

  loadOptions(): void {
    this.pricingService.getOptions().subscribe({
      next: (data) => {
        this.products = data.products;
        this.currencies = data.currencies;

        if (this.products.length > 0) {
          this.selectedProduct = this.products[0].name;
        }
        if (this.currencies.length > 0) {
          this.selectedCurrency = this.currencies[0].code;
          this.exchangeRate = this.currencies[0].approxRate;
        }
      },
      error: (err) => {
        console.error('Failed to load options from backend:', err);
        // Fallback default catalogs
        this.products = [
          { id: 'macbook_pro_14', name: 'Apple MacBook Pro 14"', category: 'Laptop', brand: 'Apple', benchmarkUsdPrice: 1599.0 },
          { id: 'dell_xps_15', name: 'Dell XPS 15', category: 'Laptop', brand: 'Dell', benchmarkUsdPrice: 1299.0 },
          { id: 'ipad_pro_m4', name: 'Apple iPad Pro M4', category: 'Tablet', brand: 'Apple', benchmarkUsdPrice: 999.0 }
        ];
        this.currencies = [
          { code: 'EUR', name: 'Euro', symbol: '€', approxRate: 0.85 },
          { code: 'GBP', name: 'British Pound', symbol: '£', approxRate: 0.77 },
          { code: 'JPY', name: 'Japanese Yen', symbol: '¥', approxRate: 150.0 }
        ];
      }
    });
  }

  onCurrencyChange(): void {
    const match = this.currencies.find(c => c.code === this.selectedCurrency);
    if (match) {
      this.exchangeRate = match.approxRate;
    }
  }

  get synthesizedQuery(): string {
    const match = this.currencies.find(c => c.code === this.selectedCurrency);
    const currName = match?.name || this.selectedCurrency;
    const rateStr = this.exchangeRate.toString();
    return `What is the total retail purchase price (MSRP) of a new entry-level ${this.selectedProduct} in USD? Do not use monthly financing. How much would it cost in ${currName} (${this.selectedCurrency}) if the exchange rate is ${rateStr} ${this.selectedCurrency} for 1 USD? Use the Calculator tool to do the conversion. Do not do it yourself.`;
  }

  executeCoordination(): void {
    this.loading = true;
    this.error = null;
    this.result = null;
    const startTime = performance.now();

    const match = this.currencies.find(c => c.code === this.selectedCurrency);
    const request = {
      product: this.selectedProduct,
      currency_code: this.selectedCurrency,
      currency_name: match?.name || this.selectedCurrency,
      exchange_rate: this.exchangeRate,
      custom_query: this.synthesizedQuery
    };

    this.pricingService.computePricing(request).subscribe({
      next: (res) => {
        this.result = res;
        this.executionDurationMs = Math.round(performance.now() - startTime);
        this.loading = false;
      },
      error: (err) => {
        console.error('Pricing coordination error:', err);
        this.error = 'Failed to execute Drools rules: Ensure Spring Boot is running on port 8080.';
        this.loading = false;
      }
    });
  }

  getKindClass(kind: string): string {
    switch (kind.toLowerCase()) {
      case 'thought': return 'badge-purple';
      case 'action': return 'badge-blue';
      case 'observation': return 'badge-amber';
      case 'finished': return 'badge-emerald';
      default: return 'badge-blue';
    }
  }

  getKindIcon(kind: string): string {
    switch (kind.toLowerCase()) {
      case 'thought': return '🧠';
      case 'action': return '⚡';
      case 'observation': return '👁️';
      case 'finished': return '🎯';
      default: return '📌';
    }
  }
}
