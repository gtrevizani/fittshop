import { Component, EventEmitter, HostListener, Output } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RecommendationService } from '../../services/recommendation.service';
import { CartService } from '../../services/cart.service';
import { AuthService } from '../../services/auth.service';
import { ToastService } from '../../services/toast.service';
import { AthleteProfilePayload, RecommendationResult } from '../../models/recommendation.model';
import { Product } from '../../models/product.model';

@Component({
  selector: 'app-recommender',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './recommender.component.html',
  styleUrls: ['./recommender.component.css']
})
export class RecommenderComponent {
  @Output() closeEvent = new EventEmitter<void>();

  goal: 'HIPERTROFIA' | 'DEFINICAO' | 'FORCA_POWERLIFTING' | 'RESISTENCIA_CARDIO' = 'HIPERTROFIA';
  weightKg = 78;
  heightMeters = 1.78;
  weeklyWorkouts = 5;
  experienceLevel = 'INTERMEDIARIO';

  isCalculating = false;
  result: RecommendationResult | null = null;

  constructor(
    private recommendationService: RecommendationService,
    private cartService: CartService,
    public authService: AuthService,
    private toastService: ToastService
  ) {}

  @HostListener('document:keydown.escape')
  onEscape(): void {
    this.closeEvent.emit();
  }

  calculate(): void {
    this.isCalculating = true;
    const payload: AthleteProfilePayload = {
      goal: this.goal,
      weightKg: Number(this.weightKg),
      heightMeters: Number(this.heightMeters),
      weeklyWorkouts: Number(this.weeklyWorkouts),
      experienceLevel: this.experienceLevel
    };

    this.recommendationService.calculate(payload).subscribe({
      next: (res) => {
        this.result = res;
        this.isCalculating = false;
        this.toastService.success('✨ Plano NutriFit IA calculado com base no seu biotipo!');
      },
      error: () => {
        this.isCalculating = false;
        this.toastService.error('Erro ao calcular plano nutricional.');
      }
    });
  }

  addComboToCart(): void {
    if (!this.authService.isAuthenticated()) {
      this.closeEvent.emit();
      this.authService.openLoginModal('🔒 Para adicionar o Combo NutriFit IA à sua sacola, acesse sua conta de Atleta!');
      return;
    }
    if (this.result && this.result.recommendedProducts) {
      this.cartService.addCombo(this.result.recommendedProducts);
      this.toastService.success('🔥 Stack NutriFit IA adicionado à sua sacola!');
      this.closeEvent.emit();
    }
  }

  addProduct(product: Product): void {
    if (!this.authService.isAuthenticated()) {
      this.closeEvent.emit();
      this.authService.openLoginModal('🔒 Para adicionar itens à sua sacola, acesse sua conta de Atleta!');
      return;
    }
    this.cartService.addItem(product, 1);
  }
}
