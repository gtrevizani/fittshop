import { Component, EventEmitter, OnInit, Output } from '@angular/core';
import { CommonModule } from '@angular/common';
import { HttpClient } from '@angular/common/http';
import { AuthService } from '../../services/auth.service';
import { OrderData } from '../../models/cart.model';
import { environment } from '../../../environments/environment';

@Component({
  selector: 'app-orders-modal',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './orders-modal.component.html',
  styleUrls: ['./orders-modal.component.css']
})
export class OrdersModalComponent implements OnInit {
  @Output() closeEvent = new EventEmitter<void>();

  orders: OrderData[] = [];
  isLoading = true;
  errorMessage = '';
  selectedDanfe: any = null;
  isLoadingDanfe = false;
  danfeError = '';
  copiedKey = false;

  constructor(
    public authService: AuthService,
    private http: HttpClient
  ) {}

  openDanfe(orderNumber: string): void {
    this.isLoadingDanfe = true;
    this.danfeError = '';
    this.selectedDanfe = null;

    this.http.get<any>(`${environment.apiUrl}/orders/${orderNumber}/danfe`).subscribe({
      next: (data) => {
        this.isLoadingDanfe = false;
        this.selectedDanfe = data;
      },
      error: (err) => {
        this.isLoadingDanfe = false;
        this.danfeError = err.error?.message || 'Não foi possível carregar a nota fiscal deste pedido.';
      }
    });
  }

  closeDanfe(): void {
    this.selectedDanfe = null;
    this.danfeError = '';
    this.copiedKey = false;
  }

  copyAccessKey(key: string): void {
    if (!key) return;
    navigator.clipboard.writeText(key.replace(/\s+/g, ''));
    this.copiedKey = true;
    setTimeout(() => this.copiedKey = false, 2500);
  }

  printDanfe(): void {
    window.print();
  }

  ngOnInit(): void {
    this.loadOrders();
  }

  loadOrders(): void {
    this.isLoading = true;
    this.errorMessage = '';

    const user = this.authService.currentUser();
    let url = `${environment.apiUrl}/orders`;
    if (user && user.userId) {
      url += `?customerId=${user.userId}`;
    }

    this.http.get<OrderData[]>(url).subscribe({
      next: (data) => {
        this.isLoading = false;
        this.orders = data || [];
      },
      error: (err) => {
        this.isLoading = false;
        console.warn('Erro ao buscar pedidos:', err);
        // Fallback display if database is clean
        this.orders = [];
      }
    });
  }

  getStatusBadgeClass(status: string): string {
    switch (status) {
      case 'PENDENTE_PAGAMENTO': return 'badge-pending';
      case 'PAGO': return 'badge-paid';
      case 'EM_SEPARACAO': return 'badge-processing';
      case 'EXPEDIDO': return 'badge-shipped';
      case 'ENTREGUE': return 'badge-delivered';
      case 'CANCELADO': return 'badge-cancelled';
      default: return 'badge-default';
    }
  }

  getStatusLabel(status: string): string {
    switch (status) {
      case 'PENDENTE_PAGAMENTO': return '⏳ Aguardando Pagamento';
      case 'PAGO': return '✅ Pago & Confirmado';
      case 'EM_SEPARACAO': return '📦 Em Separação no CD';
      case 'EXPEDIDO': return '🚚 Enviado / A Caminho';
      case 'ENTREGUE': return '🏆 Entregue com Sucesso';
      case 'CANCELADO': return '❌ Cancelado';
      default: return status;
    }
  }
}
