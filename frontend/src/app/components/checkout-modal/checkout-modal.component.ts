import { Component, OnDestroy, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { CartService } from '../../services/cart.service';
import { CheckoutRequestPayload, CheckoutResult } from '../../models/cart.model';

import { HttpClient } from '@angular/common/http';

@Component({
  selector: 'app-checkout-modal',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './checkout-modal.component.html',
  styleUrls: ['./checkout-modal.component.css']
})
export class CheckoutModalComponent implements OnInit, OnDestroy {
  step: 'FORM' | 'CONFIRMATION' = 'FORM';

  customerName = 'Lucas Atleta';
  customerEmail = 'atleta@fittshop.com.br';
  customerCpf = '123.456.789-00';
  customerPhone = '(11) 98888-7777';
  deliveryZipCode = '01310-100';
  deliveryAddress = 'Av. Paulista, 1500 - Apto 82, Bela Vista, São Paulo - SP';
  paymentMethod: 'PIX' | 'CREDIT_CARD' | 'BOLETO' = 'PIX';
  sellerCode = '';

  isSubmitting = false;
  isSearchingCep = false;
  cepErrorMessage = '';
  cpfErrorMessage = '';
  orderResult: CheckoutResult | null = null;
  copiedPix = false;

  // 15-minute countdown timer
  remainingSeconds = 900;
  timerInterval: any;

  constructor(
    public cartService: CartService,
    private http: HttpClient
  ) {}

  ngOnInit(): void {
    this.startCountdown();
  }

  ngOnDestroy(): void {
    if (this.timerInterval) clearInterval(this.timerInterval);
  }

  startCountdown(): void {
    this.remainingSeconds = 900;
    this.timerInterval = setInterval(() => {
      if (this.remainingSeconds > 0) {
        this.remainingSeconds--;
      }
    }, 1000);
  }

  formatTime(seconds: number): string {
    const mins = Math.floor(seconds / 60);
    const secs = seconds % 60;
    return `${mins.toString().padStart(2, '0')}:${secs.toString().padStart(2, '0')}`;
  }

  onCepChange(): void {
    const cleanCep = this.deliveryZipCode.replace(/\D/g, '');
    if (cleanCep.length === 8) {
      this.isSearchingCep = true;
      this.cepErrorMessage = '';
      this.http.get<any>(`https://viacep.com.br/ws/${cleanCep}/json/`).subscribe({
        next: (data) => {
          this.isSearchingCep = false;
          if (data && !data.erro) {
            this.deliveryAddress = `${data.logradouro}, nº  - ${data.bairro}, ${data.localidade} - ${data.uf}`;
          } else {
            this.cepErrorMessage = 'CEP não encontrado. Preencha o endereço manualmente.';
          }
        },
        error: () => {
          this.isSearchingCep = false;
        }
      });
    }
  }

  validateCpf(cpf: string): boolean {
    const clean = cpf.replace(/\D/g, '');
    if (clean.length !== 11 || /^(\d)\1{10}$/.test(clean)) return false;
    let sum = 0, rest;
    for (let i = 1; i <= 9; i++) sum += parseInt(clean.substring(i - 1, i)) * (11 - i);
    rest = (sum * 10) % 11;
    if (rest === 10 || rest === 11) rest = 0;
    if (rest !== parseInt(clean.substring(9, 10))) return false;
    sum = 0;
    for (let i = 1; i <= 10; i++) sum += parseInt(clean.substring(i - 1, i)) * (12 - i);
    rest = (sum * 10) % 11;
    if (rest === 10 || rest === 11) rest = 0;
    return rest === parseInt(clean.substring(10, 11));
  }

  submitOrder(): void {
    this.cpfErrorMessage = '';
    const cleanCpf = this.customerCpf.replace(/\D/g, '');
    if (cleanCpf.length === 11 && !this.validateCpf(this.customerCpf)) {
      this.cpfErrorMessage = 'CPF inválido. Verifique os dígitos digitados.';
      return;
    }
    this.isSubmitting = true;

    const payload: CheckoutRequestPayload = {
      customerName: this.customerName,
      customerEmail: this.customerEmail,
      customerCpf: this.customerCpf,
      customerPhone: this.customerPhone,
      deliveryZipCode: this.deliveryZipCode,
      deliveryAddress: this.deliveryAddress,
      paymentMethod: this.paymentMethod,
      sellerCode: this.sellerCode || undefined,
      couponCode: this.cartService.couponCode() || undefined,
      fittCoinsToRedeem: this.cartService.fittCoinsToRedeem(),
      items: this.cartService.items().map(i => ({
        productId: i.product.id,
        quantity: i.quantity
      }))
    };

    this.cartService.submitCheckout(payload).subscribe({
      next: (result) => {
        this.orderResult = result;
        this.cartService.lastOrderResult.set(result);
        this.step = 'CONFIRMATION';
        this.isSubmitting = false;
        this.cartService.clearCart();
      },
      error: () => {
        this.isSubmitting = false;
      }
    });
  }

  copyPixPayload(): void {
    if (this.orderResult?.pixCopiaECola) {
      navigator.clipboard.writeText(this.orderResult.pixCopiaECola);
      this.copiedPix = true;
      setTimeout(() => (this.copiedPix = false), 2500);
    }
  }

  simulatePaymentWebhook(): void {
    if (this.orderResult) {
      this.orderResult.status = 'PAGO';
      this.orderResult.message = '🎉 Pagamento aprovado! Seu pedido já foi enviado para separação pelo Estoquista.';
    }
  }

  close(): void {
    this.cartService.isCheckoutModalOpen.set(false);
  }
}
