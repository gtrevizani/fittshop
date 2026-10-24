import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { HeaderComponent } from './components/header/header.component';
import { HeroComponent } from './components/hero/hero.component';
import { CatalogComponent } from './components/catalog/catalog.component';
import { RecommenderComponent } from './components/recommender/recommender.component';
import { CartDrawerComponent } from './components/cart-drawer/cart-drawer.component';
import { CheckoutModalComponent } from './components/checkout-modal/checkout-modal.component';
import { BackofficeComponent } from './components/backoffice/backoffice.component';
import { AuthModalComponent } from './components/auth-modal/auth-modal.component';
import { CartService } from './services/cart.service';
import { AuthService } from './services/auth.service';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [
    CommonModule,
    HeaderComponent,
    HeroComponent,
    CatalogComponent,
    RecommenderComponent,
    CartDrawerComponent,
    CheckoutModalComponent,
    BackofficeComponent,
    AuthModalComponent
  ],
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})
export class AppComponent {
  selectedGoal = 'TODOS';
  searchQuery = '';
  showRecommenderModal = false;
  showBackoffice = false;
  showAuthModal = false;
  showOrdersModal = false;

  constructor(public cartService: CartService, public authService: AuthService) {}

  onGoalSelected(goal: string): void {
    this.selectedGoal = goal;
  }

  onSearch(query: string): void {
    this.searchQuery = query;
  }

  toggleBackoffice(): void {
    if (!this.authService.isStaff()) {
      this.showBackoffice = false;
      return;
    }
    this.showBackoffice = !this.showBackoffice;
  }
}
