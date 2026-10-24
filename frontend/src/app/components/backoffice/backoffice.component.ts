import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ProductService } from '../../services/product.service';
import { AuthService, UserRole } from '../../services/auth.service';
import { Product } from '../../models/product.model';

@Component({
  selector: 'app-backoffice',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './backoffice.component.html',
  styleUrls: ['./backoffice.component.css']
})
export class BackofficeComponent implements OnInit {
  activeTab: 'ESTOQUE' | 'CAIXA' | 'VENDEDORAS' | 'GERENCIA' = 'ESTOQUE';

  // Estoquista Form
  selectedProductId: number | null = null;
  replenishQuantity = 20;
  newBatchCode = 'LOTE-FITT-2026';
  newExpirationDate = '2028-06-30';
  estoqueSuccessMessage = '';

  // Caixa state
  caixaOrders = [
    { orderNumber: 'FITT-982142', customer: 'Lucas Atleta', total: 279.90, method: 'PIX', status: 'PAGO', itemsCount: 3, tracking: 'FIT849201BR' },
    { orderNumber: 'FITT-982141', customer: 'Renata Fit', total: 149.90, method: 'CREDIT_CARD', status: 'EM_SEPARACAO', itemsCount: 1, tracking: 'FIT849190BR' },
    { orderNumber: 'FITT-982140', customer: 'Felipe Cross', total: 329.80, method: 'PIX', status: 'AGUARDANDO_PAGAMENTO', itemsCount: 2, tracking: 'FIT849182BR' },
    { orderNumber: 'FITT-982139', customer: 'Carla Musculacao', total: 189.90, method: 'BOLETO', status: 'EXPEDIDO', itemsCount: 1, tracking: 'FIT849175BR' }
  ];

  // Vendedoras state
  sellers = [
    { code: 'CAMILA04', name: 'Camila Consultora', salesCount: 18, totalSales: 4890.00, commissionRate: 0.04, commissionEarned: 195.60 },
    { code: 'BEATRIZ02', name: 'Beatriz Fitness', salesCount: 12, totalSales: 3240.00, commissionRate: 0.04, commissionEarned: 129.60 },
    { code: 'JULIANA09', name: 'Juliana Nutri', salesCount: 24, totalSales: 7120.00, commissionRate: 0.05, commissionEarned: 356.00 }
  ];

  constructor(public productService: ProductService, public authService: AuthService) {}

  ngOnInit(): void {
    if (this.productService.getProducts().length > 0) {
      this.selectedProductId = this.productService.getProducts()[0].id;
    }
  }

  processReplenishment(): void {
    if (!this.selectedProductId || this.replenishQuantity <= 0) return;

    this.productService.adjustStockLocal(this.selectedProductId, Number(this.replenishQuantity));
    this.estoqueSuccessMessage = `✅ Fardo recebido com sucesso! +${this.replenishQuantity} unidades adicionadas ao estoque com lote ${this.newBatchCode}.`;

    setTimeout(() => {
      this.estoqueSuccessMessage = '';
    }, 4000);
  }

  confirmCaixaPayment(orderNumber: string): void {
    const order = this.caixaOrders.find(o => o.orderNumber === orderNumber);
    if (order) {
      order.status = 'PAGO';
    }
  }

  dispatchOrder(orderNumber: string): void {
    const order = this.caixaOrders.find(o => o.orderNumber === orderNumber);
    if (order) {
      order.status = 'EXPEDIDO';
    }
  }

  getSelectedProduct(): Product | undefined {
    return this.productService.getProducts().find(p => p.id === this.selectedProductId);
  }

  calculateTotalRevenue(): number {
    return this.caixaOrders.filter(o => o.status === 'PAGO' || o.status === 'EXPEDIDO').reduce((sum, o) => sum + o.total, 0);
  }
}
