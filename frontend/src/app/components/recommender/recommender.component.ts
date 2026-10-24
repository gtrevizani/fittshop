import { Component, EventEmitter, Output } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RecommendationService } from '../../services/recommendation.service';
import { CartService } from '../../services/cart.service';
import { AuthService } from '../../services/auth.service';
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
    public authService: AuthService
  ) {}

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
      },
      error: () => {
        this.isCalculating = false;
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
