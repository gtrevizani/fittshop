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

  constructor(public cartService: CartService) {}

  applyCoupon(): void {
    if (this.cartService.applyCoupon(this.couponInput)) {
      this.couponMessage = 'Cupom aplicado com sucesso!';
      this.couponSuccess = true;
    } else {
      this.couponMessage = 'Cupom inválido. Tente FITT10 ou MONSTRO15';
      this.couponSuccess = false;
    }
  }

  onFittCoinsChange(): void {
    this.cartService.setFittCoinsRedemption(this.fittCoinsSlider);
  }

  onCalculateFreight(): void {
    if (this.zipCodeInput.length >= 8) {
      this.cartService.calculateFreight(this.zipCodeInput);
    }
  }

  proceedToCheckout(): void {
    this.cartService.isCartOpen.set(false);
    this.cartService.isCheckoutModalOpen.set(true);
  }
}
