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
  @Output() openOrdersEvent = new EventEmitter<void>();

  searchTerm = '';

  constructor(public cartService: CartService, public authService: AuthService) {}

  onSearch(): void {
    this.searchEvent.emit(this.searchTerm);
  }

  onLogout(): void {
    this.authService.logout();
  }
}
