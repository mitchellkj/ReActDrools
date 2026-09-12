export interface Product {
  id: string;
  name: string;
  category: string;
  brand: string;
  benchmarkUsdPrice: number;
}

export interface CurrencyExchange {
  code: string;
  name: string;
  symbol: string;
  approxRate: number;
}

export interface OptionsResponse {
  products: Product[];
  currencies: CurrencyExchange[];
}

export interface PricingRequest {
  product: string;
  currency_code: string;
  currency_name?: string;
  exchange_rate?: number;
  custom_query?: string;
  session_id?: string;
}

export interface AuditItem {
  step: number;
  kind: 'Thought' | 'Action' | 'Observation' | 'Finished' | 'Fact';
  content: string;
}

export interface PricingResponse {
  success: boolean;
  session_id: string;
  product: string;
  currency: string;
  exchange_rate: number;
  query: string;
  final_answer: string;
  status: string;
  cached: boolean;
  knowledge_source?: string;
  query_hash?: string;
  rules_fired: number;
  audit_facts: AuditItem[];
}
