import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { CartService } from '../../services/cart.service';
import { AuthService } from '../../services/auth.service';

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

  constructor(
    public cartService: CartService,
    public authService: AuthService
  ) {}

  applyCoupon(): void {
    if (!this.couponInput.trim()) return;
    this.isValidatingCoupon = true;
    this.couponMessage = 'Validando código na nuvem FittShop...';

    const currentUser = this.authService.currentUser();
    const userCpf = currentUser ? (currentUser as any).cpf : '';

    this.cartService.applyCoupon(this.couponInput, userCpf).subscribe({
      next: (res) => {
        this.isValidatingCoupon = false;
        if (res && res.valid) {
          if (res.discountAmount !== undefined) {
            this.couponMessage = `✅ Cupom ${res.code} aplicado: R$ ${res.discountAmount.toFixed(2)} OFF (${res.description})`;
          } else {
            const discountDesc = (res.discountPercentage * 100).toFixed(0);
            this.couponMessage = `✅ Consultor ${res.affiliateCode} aplicado (${discountDesc}% OFF - ${res.affiliateName})`;
          }
          this.couponSuccess = true;
        } else {
          this.couponMessage = res?.message || 'Código de consultor ou cupom não encontrado.';
          this.couponSuccess = false;
        }
      },
      error: (err) => {
        this.isValidatingCoupon = false;
        this.couponMessage = err.error?.message || 'Erro de conexão ao validar cupom na nuvem.';
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
    if (!this.authService.isAuthenticated()) {
      this.cartService.isCartOpen.set(false);
      this.authService.openLoginModal('🔒 Para avançar ao Checkout Seguro e emitir sua reserva atômica de estoque, acesse sua conta ou cadastre-se!');
      return;
    }
    this.cartService.isCartOpen.set(false);
    this.cartService.isCheckoutModalOpen.set(true);
  }
}
