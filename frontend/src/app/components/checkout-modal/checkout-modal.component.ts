import { Component, HostListener, OnDestroy, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { CartService } from '../../services/cart.service';
import { AuthService } from '../../services/auth.service';
import { ToastService } from '../../services/toast.service';
import { CheckoutRequestPayload, CheckoutResult } from '../../models/cart.model';
import { HttpClient } from '@angular/common/http';
import { environment } from '../../../environments/environment';

@Component({
  selector: 'app-checkout-modal',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './checkout-modal.component.html',
  styleUrls: ['./checkout-modal.component.css']
})
export class CheckoutModalComponent implements OnInit, OnDestroy {
  step: 'FORM' | 'CONFIRMATION' = 'FORM';

  customerName = '';
  customerEmail = '';
  customerCpf = '';
  customerPhone = '';
  deliveryZipCode = '';
  deliveryAddress = '';
  paymentMethod: 'PIX' | 'CREDIT_CARD' | 'BOLETO' = 'PIX';
  sellerCode = '';

  isSubmitting = false;
  isSearchingCep = false;
  cepErrorMessage = '';
  cpfErrorMessage = '';
  submitErrorMessage = '';
  warmingUpNotice = false;
  orderResult: CheckoutResult | null = null;
  copiedPix = false;
  isCheckingPayment = false;

  // 15-minute countdown timer
  remainingSeconds = 900;
  timerInterval: any;
  pollingInterval: any;

  constructor(
    public cartService: CartService,
    public authService: AuthService,
    private toastService: ToastService,
    private http: HttpClient
  ) {}

  @HostListener('document:keydown.escape')
  onEscape(): void {
    this.close();
  }

  ngOnInit(): void {
    if (!this.authService.isAuthenticated()) {
      this.cartService.isCheckoutModalOpen.set(false);
      this.authService.openLoginModal('🔒 Para acessar o Checkout Seguro, identifique-se com sua conta de Atleta.');
      return;
    }
    const user = this.authService.currentUser();
    if (user) {
      this.customerName = user.name;
      this.customerEmail = user.email;
    }
    this.startCountdown();
  }

  ngOnDestroy(): void {
    if (this.timerInterval) clearInterval(this.timerInterval);
    if (this.pollingInterval) clearInterval(this.pollingInterval);
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
    this.submitErrorMessage = '';
    this.warmingUpNotice = false;
    const cleanCpf = this.customerCpf.replace(/\D/g, '');
    if (cleanCpf.length !== 11 || !this.validateCpf(this.customerCpf)) {
      this.cpfErrorMessage = 'CPF inválido: os dígitos verificadores informados não conferem com a Receita Federal.';
      return;
    }
    this.isSubmitting = true;

    const warmTimer = setTimeout(() => {
      if (this.isSubmitting) {
        this.warmingUpNotice = true;
      }
    }, 3500);

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
        clearTimeout(warmTimer);
        this.orderResult = result;
        this.cartService.lastOrderResult.set(result);
        this.step = 'CONFIRMATION';
        this.isSubmitting = false;
        this.warmingUpNotice = false;
        this.cartService.clearCart();
        this.startPaymentPolling();
      },
      error: (err) => {
        clearTimeout(warmTimer);
        this.isSubmitting = false;
        this.warmingUpNotice = false;
        const msg = err?.error?.message || err?.error?.error;
        if (msg) {
          this.submitErrorMessage = msg;
        } else if (err?.status === 0 || err?.status === 503 || err?.status === 504) {
          this.submitErrorMessage = 'Os servidores em nuvem podem estar acordando (cold start) ou a conexão oscilou. Por favor, aguarde alguns instantes e tente novamente.';
        } else {
          this.submitErrorMessage = `Erro de comunicação com o servidor (${err?.status || '500'}). Tente novamente em instantes.`;
        }
      }
    });
  }

  startPaymentPolling(): void {
    if (this.pollingInterval) clearInterval(this.pollingInterval);
    if (!this.orderResult || this.orderResult.status === 'PAGO') return;

    this.pollingInterval = setInterval(() => {
      this.checkPaymentStatus();
    }, 4000);
  }

  checkPaymentStatus(): void {
    if (!this.orderResult || this.orderResult.status === 'PAGO') {
      if (this.pollingInterval) clearInterval(this.pollingInterval);
      return;
    }

    this.isCheckingPayment = true;
    this.http.get<any>(`${environment.apiUrl}/orders/${this.orderResult.orderNumber}`).subscribe({
      next: (order) => {
        this.isCheckingPayment = false;
        if (order && (order.status === 'PAGO' || order.status === 'EM_SEPARACAO' || order.status === 'EXPEDIDO' || order.status === 'ENTREGUE')) {
          this.orderResult!.status = 'PAGO';
          this.orderResult!.message = '🎉 Pagamento aprovado via PIX / Banco Central! Seu pedido já está sendo preparado pela equipe do galpão FittShop.';
          this.toastService.success('🎉 Pagamento PIX identificado! Seu pedido está em separação.');
          if (this.pollingInterval) clearInterval(this.pollingInterval);
        }
      },
      error: () => {
        this.isCheckingPayment = false;
      }
    });
  }

  copyPixPayload(): void {
    if (this.orderResult?.pixCopiaECola) {
      navigator.clipboard.writeText(this.orderResult.pixCopiaECola);
      this.copiedPix = true;
      this.toastService.success('📋 Chave PIX copiada para a área de transferência!');
      setTimeout(() => (this.copiedPix = false), 2500);
    }
  }

  simulatePaymentWebhook(): void {
    if (!this.orderResult) return;
    this.isCheckingPayment = true;
    this.http.post<any>(`${environment.apiUrl}/checkout/confirm/${this.orderResult.orderNumber}`, {}).subscribe({
      next: () => {
        this.checkPaymentStatus();
      },
      error: () => {
        this.isCheckingPayment = false;
      }
    });
  }

  close(): void {
    if (this.pollingInterval) clearInterval(this.pollingInterval);
    this.cartService.isCheckoutModalOpen.set(false);
  }
}
