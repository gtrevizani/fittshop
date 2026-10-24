import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { CartService } from '../../services/cart.service';

@Component({
  selector: 'app-cart-drawer',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './cart-drawer.component.html',
  styleUrls: ['./cart-drawer.component.css']
})
export class CartDrawerComponent {
  couponInput = '';
  couponMessage = '';
  couponSuccess = false;

  zipCodeInput = '';
  fittCoinsSlider = 0;
  Math = Math;

  isValidatingCoupon = false;
  isCalculatingFreight = false;
  freightErrorMessage = '';

  constructor(public cartService: CartService) {}

  applyCoupon(): void {
    if (!this.couponInput.trim()) return;
    this.isValidatingCoupon = true;
    this.couponMessage = 'Validando código na nuvem FittShop...';

    this.cartService.applyCoupon(this.couponInput).subscribe({
      next: (res) => {
        this.isValidatingCoupon = false;
        if (res.valid) {
          const discountDesc = (res.discountPercentage * 100).toFixed(0);
          this.couponMessage = `✅ Código ${res.affiliateCode} aplicado (${discountDesc}% OFF - ${res.affiliateName})`;
          this.couponSuccess = true;
        } else {
          this.couponMessage = 'Código de consultor ou cupom não encontrado.';
          this.couponSuccess = false;
        }
      },
      error: () => {
        this.isValidatingCoupon = false;
        this.couponMessage = 'Erro de conexão ao validar cupom na nuvem.';
        this.couponSuccess = false;
      }
    });
  }

  onFittCoinsChange(): void {
    this.cartService.setFittCoinsRedemption(this.fittCoinsSlider);
  }

  onCalculateFreight(): void {
    const clean = this.zipCodeInput.replace(/\D/g, '');
    if (clean.length === 8) {
      this.isCalculatingFreight = true;
      this.freightErrorMessage = '';
      this.cartService.calculateFreight(clean).subscribe({
        next: () => {
          this.isCalculatingFreight = false;
        },
        error: () => {
          this.isCalculatingFreight = false;
          this.freightErrorMessage = 'Falha ao cotar frete junto à transportadora.';
        }
      });
    }
  }

  proceedToCheckout(): void {
    this.cartService.isCartOpen.set(false);
    this.cartService.isCheckoutModalOpen.set(true);
  }
}
