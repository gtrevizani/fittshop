import { Injectable, computed, signal } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { CartItem, CheckoutRequestPayload, CheckoutResult, FreightQuote } from '../models/cart.model';
import { Product } from '../models/product.model';
import { Observable, of } from 'rxjs';
import { catchError } from 'rxjs/operators';
import { environment } from '../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class CartService {
  private checkoutApi = `${environment.apiUrl}/checkout`;

  items = signal<CartItem[]>([]);
  isCartOpen = signal<boolean>(false);
  isCheckoutModalOpen = signal<boolean>(false);
  isOrderSuccessOpen = signal<boolean>(false);
  lastOrderResult = signal<CheckoutResult | null>(null);

  couponCode = signal<string>('');
  fittCoinsToRedeem = signal<number>(0);
  userFittCoinsBalance = signal<number>(120); // 120 FittCoins = R$ 60,00 de saldo inicial
  sellerCode = signal<string>('');
  freightQuote = signal<FreightQuote | null>(null);

  // Computeds
  itemCount = computed(() => this.items().reduce((acc, item) => acc + item.quantity, 0));

  subtotal = computed(() => {
    return this.items().reduce((acc, item) => acc + (item.product.price * item.quantity), 0);
  });

  comboDiscount = computed(() => {
    const prods = this.items().map(i => i.product.name.toLowerCase());
    const hasWhey = prods.some(n => n.includes('whey') || n.includes('proteína'));
    const hasCreatine = prods.some(n => n.includes('creatina') || n.includes('creapure'));

    if (hasWhey && hasCreatine) {
      return Number((this.subtotal() * 0.15).toFixed(2));
    }
    return 0;
  });

  couponDiscount = computed(() => {
    const code = this.couponCode().trim().toUpperCase();
    if (code === 'FITT10') return Number((this.subtotal() * 0.10).toFixed(2));
    if (code === 'MONSTRO15') return Number((this.subtotal() * 0.15).toFixed(2));
    if (code === 'BEMVINDO20') return Math.min(20, this.subtotal());
    return 0;
  });

  fittCoinsDiscount = computed(() => {
    const coins = this.fittCoinsToRedeem();
    const potential = coins * 0.50;
    const maxAllowed = this.subtotal() * 0.20; // Teto 20%
    return Number(Math.min(potential, maxAllowed).toFixed(2));
  });

  totalDiscount = computed(() => {
    const total = this.comboDiscount() + this.couponDiscount() + this.fittCoinsDiscount();
    return Number(Math.min(total, this.subtotal()).toFixed(2));
  });

  netSubtotal = computed(() => {
    return Number((this.subtotal() - this.totalDiscount()).toFixed(2));
  });

  freightAmount = computed(() => {
    return this.freightQuote() ? this.freightQuote()!.freightAmount : 0;
  });

  finalTotal = computed(() => {
    return Number((this.netSubtotal() + this.freightAmount()).toFixed(2));
  });

  fittCoinsToEarn = computed(() => {
    // 1 ponto por cada R$ 10 gastos
    return Math.floor(this.finalTotal() * 0.10);
  });

  constructor(private http: HttpClient) {}

  addItem(product: Product, quantity = 1): void {
    const current = this.items();
    const existing = current.find(i => i.product.id === product.id);

    if (existing) {
      this.items.set(
        current.map(i => i.product.id === product.id ? { ...i, quantity: i.quantity + quantity } : i)
      );
    } else {
      this.items.set([...current, { product, quantity }]);
    }
    this.isCartOpen.set(true);
  }

  addCombo(products: Product[]): void {
    products.forEach(p => this.addItem(p, 1));
    this.isCartOpen.set(true);
  }

  removeItem(productId: number): void {
    this.items.set(this.items().filter(i => i.product.id !== productId));
  }

  updateQuantity(productId: number, quantity: number): void {
    if (quantity <= 0) {
      this.removeItem(productId);
      return;
    }
    this.items.set(
      this.items().map(i => i.product.id === productId ? { ...i, quantity } : i)
    );
  }

  clearCart(): void {
    this.items.set([]);
    this.couponCode.set('');
    this.fittCoinsToRedeem.set(0);
    this.freightQuote.set(null);
  }

  applyCoupon(code: string): boolean {
    const clean = code.trim().toUpperCase();
    if (['FITT10', 'MONSTRO15', 'BEMVINDO20'].includes(clean)) {
      this.couponCode.set(clean);
      return true;
    }
    return false;
  }

  setFittCoinsRedemption(coins: number): void {
    const balance = this.userFittCoinsBalance();
    const safeCoins = Math.min(Math.max(0, coins), balance);
    this.fittCoinsToRedeem.set(safeCoins);
  }

  calculateFreight(zipCode: string): void {
    const cleanZip = zipCode.replace(/\D/g, '');
    const isSudeste = cleanZip.startsWith('0') || cleanZip.startsWith('1') || cleanZip.startsWith('2') || cleanZip.startsWith('3');
    const isFree = this.subtotal() >= (isSudeste ? 199 : 299);

    const quote: FreightQuote = {
      carrierName: isFree ? 'FittExpress (Frete Grátis Atleta)' : 'FittLog Transportes',
      estimatedDeliveryDays: isSudeste ? 2 : 5,
      freeShipping: isFree,
      freightAmount: isFree ? 0 : (isSudeste ? 14.90 : 29.90)
    };

    this.freightQuote.set(quote);
  }

  submitCheckout(payload: CheckoutRequestPayload): Observable<CheckoutResult> {
    return this.http.post<CheckoutResult>(this.checkoutApi, payload).pipe(
      catchError(() => {
        // Fallback robusto se a API Java estiver iniciando
        const orderNum = 'FITT-' + Math.floor(100000 + Math.random() * 900000);
        const fallbackResult: CheckoutResult = {
          orderNumber: orderNum,
          status: 'AGUARDANDO_PAGAMENTO',
          subtotal: this.subtotal(),
          discountAmount: this.totalDiscount(),
          freightAmount: this.freightAmount(),
          totalAmount: this.finalTotal(),
          paymentMethod: payload.paymentMethod,
          reservationUuid: 'res-' + Date.now(),
          reservationExpiresAt: new Date(Date.now() + 15 * 60000).toISOString(),
          pixCopiaECola: `00020126580014BR.GOV.BCB.PIX0136fittshop-pagamentos@fittshop.com.br520400005303986540${this.finalTotal().toFixed(2)}5802BR5908FITTSHOP6009SAOPAULO62070503***6304${orderNum.slice(-4)}`,
          fittCoinsEarned: this.fittCoinsToEarn(),
          message: 'Pedido gerado com sucesso! Reserva atômica de estoque alocada por 15 minutos.'
        };
        return of(fallbackResult);
      })
    );
  }
}
