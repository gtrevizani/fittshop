import { Component, EventEmitter, Output } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { CartService } from '../../services/cart.service';
import { AuthService, UserRole } from '../../services/auth.service';

@Component({
  selector: 'app-header',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './header.component.html',
  styleUrls: ['./header.component.css']
})
export class HeaderComponent {
  @Output() searchEvent = new EventEmitter<string>();
  @Output() openRecommenderEvent = new EventEmitter<void>();
  @Output() toggleBackofficeEvent = new EventEmitter<void>();
  @Output() openAuthEvent = new EventEmitter<void>();

  searchTerm = '';

  roles: { label: string; value: UserRole; icon: string }[] = [
    { label: 'Atleta (Cliente)', value: 'ROLE_CUSTOMER', icon: '🏋️' },
    { label: 'Estoquista', value: 'ROLE_STOCKIST', icon: '📦' },
    { label: 'Caixa / PDV', value: 'ROLE_CASHIER', icon: '💳' },
    { label: 'Vendedora', value: 'ROLE_SELLER', icon: '🏷️' },
    { label: 'Gerente', value: 'ROLE_MANAGER', icon: '📊' }
  ];

  constructor(public cartService: CartService, public authService: AuthService) {}

  onSearch(): void {
    this.searchEvent.emit(this.searchTerm);
  }

  setRole(role: UserRole): void {
    this.authService.setRole(role);
    if (this.authService.isStaff()) {
      this.toggleBackofficeEvent.emit();
    }
  }
}
