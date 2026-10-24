import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { HttpClient } from '@angular/common/http';
import { ProductService } from '../../services/product.service';
import { AuthService, UserRole } from '../../services/auth.service';
import { Product } from '../../models/product.model';
import { environment } from '../../../environments/environment';

export interface AffiliateStatementItem {
  code: string;
  name: string;
  salesCount: number;
  totalSales: number;
  commissionRate: number;
  commissionEarned: number;
  pendingAmount: number;
  paidAmount: number;
}

@Component({
  selector: 'app-backoffice',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './backoffice.component.html',
  styleUrls: ['./backoffice.component.css']
})
export class BackofficeComponent implements OnInit {
  activeTab: 'ESTOQUE' | 'CAIXA' | 'VENDEDORAS' | 'GERENCIA' | 'FULFILLMENT' = 'ESTOQUE';

  // Estoquista Form
  selectedProductId: number | null = null;
  replenishQuantity = 20;
  newBatchCode = 'LOTE-FITT-2026';
  newExpirationDate = '2028-06-30';
  estoqueSuccessMessage = '';
  estoqueErrorMessage = '';

  // Caixa & Fulfillment state
  caixaOrders: any[] = [];
  isLoadingOrders = false;
  caixaActionMessage = '';
  selectedShippingLabel: any = null;

  // Vendedoras / Afiliados state
  sellers: AffiliateStatementItem[] = [];
  isLoadingSellers = false;
  searchAffiliateCode = '';
  searchedAffiliateStatement: AffiliateStatementItem | null = null;
  affiliateSearchError = '';

  // Auditoria DevSecOps state
  auditLogs: any[] = [];

  constructor(
    public productService: ProductService,
    public authService: AuthService,
    private http: HttpClient
  ) {}

  ngOnInit(): void {
    if (this.productService.getProducts().length > 0) {
      this.selectedProductId = this.productService.getProducts()[0].id;
    }
    this.fetchOrders();
    this.fetchAffiliatesData();
    this.fetchAuditLogs();
  }

  fetchAuditLogs(): void {
    this.http.get<any[]>(`${environment.apiUrl}/inventory/audit-logs`).subscribe({
      next: (logs) => this.auditLogs = logs || [],
      error: () => this.auditLogs = []
    });
  }

  fetchOrders(): void {
    this.isLoadingOrders = true;
    this.http.get<any[]>(`${environment.apiUrl}/orders`).subscribe({
      next: (orders) => {
        this.isLoadingOrders = false;
        if (orders) {
          this.caixaOrders = orders.map(o => ({
            orderNumber: o.orderNumber,
            customer: o.customerCpf ? `Atleta (${o.customerCpf})` : `Atleta #${o.customerId}`,
            deliveryAddress: o.deliveryAddress,
            deliveryZipCode: o.deliveryZipCode,
            total: o.totalAmount,
            method: o.paymentMethod,
            status: o.status,
            items: o.items || [],
            itemsCount: o.items ? o.items.length : 1,
            tracking: o.trackingCode || (o.status === 'EXPEDIDO' ? 'FIT' + Math.abs((o.id || 1) * 8921).toString() + 'BR' : 'AGUARDANDO')
          }));
        }
      },
      error: () => {
        this.isLoadingOrders = false;
      }
    });
  }

  fetchAffiliatesData(): void {
    this.isLoadingSellers = true;
    const defaultCodes = [
      { code: 'SARAH10', name: 'Sarah Nutricionista Esportiva', defaultRate: 0.10 },
      { code: 'TREINADORVIP', name: 'Prof. Rodrigo Personal Trainer', defaultRate: 0.12 },
      { code: 'FITCOACH', name: 'Coach Felipe Performance', defaultRate: 0.15 },
      { code: 'ALEX10', name: 'Alex Crossfit Head Coach', defaultRate: 0.10 }
    ];

    const results: AffiliateStatementItem[] = [];
    let pending = defaultCodes.length;

    defaultCodes.forEach(def => {
      this.http.get<any>(`${environment.apiUrl}/affiliates/${def.code}/statement`).subscribe({
        next: (stmt) => {
          results.push({
            code: def.code,
            name: def.name,
            salesCount: stmt.totalOrders || 0,
            totalSales: stmt.commissions ? stmt.commissions.reduce((s: number, c: any) => s + (c.orderTotal || 0), 0) : 0,
            commissionRate: def.defaultRate,
            commissionEarned: stmt.totalEarned || 0,
            pendingAmount: stmt.pendingAmount || 0,
            paidAmount: stmt.paidAmount || 0
          });
          pending--;
          if (pending === 0) {
            this.sellers = results;
            this.isLoadingSellers = false;
          }
        },
        error: () => {
          results.push({
            code: def.code,
            name: def.name,
            salesCount: 0,
            totalSales: 0,
            commissionRate: def.defaultRate,
            commissionEarned: 0,
            pendingAmount: 0,
            paidAmount: 0
          });
          pending--;
          if (pending === 0) {
            this.sellers = results;
            this.isLoadingSellers = false;
          }
        }
      });
    });
  }

  searchAffiliate(): void {
    if (!this.searchAffiliateCode.trim()) return;
    const clean = this.searchAffiliateCode.trim().toUpperCase();
    this.affiliateSearchError = '';
    this.searchedAffiliateStatement = null;

    this.http.get<any>(`${environment.apiUrl}/affiliates/validate/${clean}`).subscribe({
      next: (val) => {
        if (!val.valid) {
          this.affiliateSearchError = `Código "${clean}" não encontrado na base de credenciados.`;
          return;
        }

        this.http.get<any>(`${environment.apiUrl}/affiliates/${clean}/statement`).subscribe({
          next: (stmt) => {
            this.searchedAffiliateStatement = {
              code: clean,
              name: val.affiliateName,
              salesCount: stmt.totalOrders || 0,
              totalSales: stmt.commissions ? stmt.commissions.reduce((s: number, c: any) => s + (c.orderTotal || 0), 0) : 0,
              commissionRate: val.commissionRate,
              commissionEarned: stmt.totalEarned || 0,
              pendingAmount: stmt.pendingAmount || 0,
              paidAmount: stmt.paidAmount || 0
            };
          },
          error: () => {
            this.affiliateSearchError = 'Erro ao consultar extrato na nuvem.';
          }
        });
      },
      error: () => {
        this.affiliateSearchError = 'Erro ao consultar credenciais do consultor.';
      }
    });
  }

  processReplenishment(): void {
    if (!this.selectedProductId || this.replenishQuantity <= 0) return;

    this.productService.adjustStock(
      this.selectedProductId,
      Number(this.replenishQuantity),
      this.newBatchCode,
      this.newExpirationDate
    ).subscribe({
      next: () => {
        this.estoqueSuccessMessage = `✅ Fardo recebido com sucesso no banco de dados! +${this.replenishQuantity} unidades adicionadas ao estoque com lote ${this.newBatchCode}.`;
        this.estoqueErrorMessage = '';
        this.fetchAuditLogs();
        setTimeout(() => (this.estoqueSuccessMessage = ''), 4000);
      },
      error: (err) => {
        console.error('Falha ao registrar reposição de estoque no backend:', err);
        this.estoqueErrorMessage = '❌ Falha ao sincronizar com o banco de dados. A operação não pôde ser concluída.';
        this.estoqueSuccessMessage = '';
        setTimeout(() => (this.estoqueErrorMessage = ''), 5000);
      }
    });
  }

  confirmCaixaPayment(orderNumber: string): void {
    this.http.post(`${environment.apiUrl}/checkout/confirm/${orderNumber}`, {}).subscribe({
      next: () => {
        this.caixaActionMessage = `✅ Pagamento do pedido ${orderNumber} aprovado com sucesso!`;
        this.fetchOrders();
        this.fetchAffiliatesData();
        setTimeout(() => (this.caixaActionMessage = ''), 4000);
      },
      error: (err) => {
        this.caixaActionMessage = `⚠️ Erro ao aprovar pagamento (${err?.status || '500'}).`;
        setTimeout(() => (this.caixaActionMessage = ''), 4000);
      }
    });
  }

  dispatchOrder(orderNumber: string): void {
    this.http.patch(`${environment.apiUrl}/orders/${orderNumber}/status?status=EXPEDIDO`, {}).subscribe({
      next: () => {
        this.caixaActionMessage = `🚚 Pedido ${orderNumber} despachado para transportadora!`;
        this.fetchOrders();
        setTimeout(() => (this.caixaActionMessage = ''), 4000);
      },
      error: (err) => {
        this.caixaActionMessage = `⚠️ Erro ao expedir pedido (${err?.status || '500'}).`;
        setTimeout(() => (this.caixaActionMessage = ''), 4000);
      }
    });
  }

  startSeparation(orderNumber: string): void {
    this.http.patch(`${environment.apiUrl}/orders/${orderNumber}/status?status=EM_SEPARACAO`, {}).subscribe({
      next: () => {
        this.caixaActionMessage = `📦 Pedido ${orderNumber} movido para Em Separação no CD!`;
        this.fetchOrders();
        setTimeout(() => (this.caixaActionMessage = ''), 4000);
      },
      error: (err) => {
        this.caixaActionMessage = `⚠️ Erro ao atualizar status (${err?.status || '500'}).`;
        setTimeout(() => (this.caixaActionMessage = ''), 4000);
      }
    });
  }

  fulfillAndDispatch(orderNumber: string): void {
    this.http.post(`${environment.apiUrl}/orders/${orderNumber}/fulfill`, {}).subscribe({
      next: () => {
        this.caixaActionMessage = `🚚 Pedido ${orderNumber} expedido e notificação de rastreio enviada ao atleta!`;
        this.fetchOrders();
        setTimeout(() => (this.caixaActionMessage = ''), 4000);
      },
      error: (err) => {
        this.caixaActionMessage = `⚠️ Erro ao expedir pedido (${err?.status || '500'}).`;
        setTimeout(() => (this.caixaActionMessage = ''), 4000);
      }
    });
  }

  openShippingLabel(order: any): void {
    const tracking = order.tracking && order.tracking !== 'AGUARDANDO'
      ? order.tracking
      : ('BR-ME' + (10000000 + Math.floor(Math.random() * 90000000)));

    this.selectedShippingLabel = {
      orderNumber: order.orderNumber,
      trackingCode: tracking,
      recipientName: order.customer,
      recipientAddress: order.deliveryAddress || 'Av. Paulista, 1500 - Apto 82, Bela Vista, São Paulo - SP',
      recipientZip: order.deliveryZipCode || '01310-100',
      senderName: 'FITTSHOP SUPLEMENTOS NUTRICIONAIS LTDA (CD CENTRAL)',
      senderAddress: 'Av. Paulista, 1000 - Bela Vista, São Paulo - SP, CEP 01310-100',
      carrier: 'MELHOR ENVIO / CORREIOS - CONTRATO SEDEX EMPRESA',
      weightKg: '1.45 kg'
    };
  }

  closeShippingLabel(): void {
    this.selectedShippingLabel = null;
  }

  printShippingLabel(): void {
    window.print();
  }

  getSelectedProduct(): Product | undefined {
    return this.productService.getProducts().find(p => p.id === this.selectedProductId);
  }

  calculateTotalRevenue(): number {
    return this.caixaOrders.filter(o => o.status === 'PAGO' || o.status === 'EXPEDIDO').reduce((sum, o) => sum + o.total, 0);
  }
}
