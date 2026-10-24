import { Component, Input } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ProductService } from '../../services/product.service';
import { CartService } from '../../services/cart.service';
import { AuthService } from '../../services/auth.service';
import { Product } from '../../models/product.model';
import { ToastService } from '../../services/toast.service';

@Component({
  selector: 'app-catalog',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './catalog.component.html',
  styleUrls: ['./catalog.component.css']
})
export class CatalogComponent {
  @Input() selectedGoal = 'TODOS';
  @Input() searchQuery = '';

  selectedCategory = 'TODOS';
  categories = ['TODOS', 'Suplementos', 'Roupas', 'Acessórios', 'Combos'];

  expandedNutritionalId: number | null = null;

  constructor(
    public productService: ProductService,
    private cartService: CartService,
    private authService: AuthService,
    private toastService: ToastService
  ) {}

  filteredProducts(): Product[] {
    let prods = this.productService.getProducts();

    if (this.selectedCategory !== 'TODOS') {
      prods = prods.filter(p => p.category.toLowerCase() === this.selectedCategory.toLowerCase());
    }

    if (this.selectedGoal !== 'TODOS') {
      prods = prods.filter(p => p.targetGoal === this.selectedGoal);
    }

    if (this.searchQuery && this.searchQuery.trim().length > 0) {
      const q = this.searchQuery.toLowerCase();
      prods = prods.filter(p =>
        p.name.toLowerCase().includes(q) ||
        p.description.toLowerCase().includes(q) ||
        p.category.toLowerCase().includes(q)
      );
    }

    return prods;
  }

  addToCart(product: Product): void {
    if (!this.authService.isAuthenticated()) {
      this.authService.openLoginModal('🔒 Para adicionar itens ao carrinho e reservar estoque com segurança atômica, acesse ou cadastre sua conta de Atleta!');
      return;
    }
    this.cartService.addItem(product, 1);
    this.toastService.success(`🛒 "${product.name}" adicionado à sua sacola!`);
  }

  toggleNutritional(id: number): void {
    this.expandedNutritionalId = this.expandedNutritionalId === id ? null : id;
  }

  calcVd(val: number | undefined, ref: number): string {
    if (val === undefined || val === null || val === 0) return '0%';
    const pct = Math.round((val / ref) * 100);
    return `${pct}%`;
  }
}
