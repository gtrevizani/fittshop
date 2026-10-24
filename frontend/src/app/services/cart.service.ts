import { Injectable, computed, signal } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { CartItem, CheckoutRequestPayload, CheckoutResult, FreightQuote } from '../models/cart.model';
import { Product } from '../models/product.model';
import { Observable, tap } from 'rxjs';
import { environment } from '../../environments/environment';

export interface AffiliateValidateResponse {
  valid: boolean;
  affiliateCode: string;
  affiliateName: string;
  commissionRate: number;
  discountPercentage: number;
}

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
  couponDiscountRate = signal<number>(0);
  couponAffiliateName = signal<string>('');
  fittCoinsToRedeem = signal<number>(0);
  userFittCoinsBalance = signal<number>(120); // 120 FittCoins = R$ 60,00 de saldo inicial
  sellerCode = signal<string>('');
  freightQuote = signal<FreightQuote | null>(null);
  isCalculatingFreight = signal<boolean>(false);

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
    const rate = this.couponDiscountRate();
    if (rate > 0) {
      return Number((this.subtotal() * rate).toFixed(2));
    }
    // Fallback retrocompatível para cupons padrão
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

  constructor(private http: HttpClient) {
    this.loadFromStorage();
  }

  private loadFromStorage(): void {
    try {
      if (typeof window !== 'undefined' && window.localStorage) {
        const saved = localStorage.getItem('fittshop_cart_items');
        if (saved) {
          const parsed = JSON.parse(saved);
          if (Array.isArray(parsed) && parsed.length > 0) {
            this.items.set(parsed);
          }
        }
      }
    } catch (e) {
      console.warn('Erro ao carregar carrinho local:', e);
    }
  }

  private saveToStorage(): void {
    try {
      if (typeof window !== 'undefined' && window.localStorage) {
        localStorage.setItem('fittshop_cart_items', JSON.stringify(this.items()));
      }
    } catch (e) {
      console.warn('Erro ao salvar carrinho local:', e);
    }
  }

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
    this.saveToStorage();
    this.isCartOpen.set(true);
  }

  addCombo(products: Product[]): void {
    products.forEach(p => {
      const current = this.items();
      const existing = current.find(i => i.product.id === p.id);
      if (existing) {
        this.items.set(
          current.map(i => i.product.id === p.id ? { ...i, quantity: i.quantity + 1 } : i)
        );
      } else {
        this.items.set([...current, { product: p, quantity: 1 }]);
      }
    });
    this.saveToStorage();
    this.isCartOpen.set(true);
  }

  removeItem(productId: number): void {
    this.items.set(this.items().filter(i => i.product.id !== productId));
    this.saveToStorage();
  }

  updateQuantity(productId: number, quantity: number): void {
    if (quantity <= 0) {
      this.removeItem(productId);
      return;
    }
    this.items.set(
      this.items().map(i => i.product.id === productId ? { ...i, quantity } : i)
    );
    this.saveToStorage();
  }

  clearCart(): void {
    this.items.set([]);
    this.couponCode.set('');
    this.couponDiscountRate.set(0);
    this.couponAffiliateName.set('');
    this.fittCoinsToRedeem.set(0);
    this.freightQuote.set(null);
    this.saveToStorage();
  }

  applyCoupon(code: string): Observable<AffiliateValidateResponse> {
    const clean = code.trim().toUpperCase();
    return this.http.get<AffiliateValidateResponse>(`${environment.apiUrl}/affiliates/validate/${clean}`).pipe(
      tap((res) => {
        if (res.valid) {
          this.couponCode.set(res.affiliateCode);
          this.couponDiscountRate.set(res.discountPercentage);
          this.couponAffiliateName.set(res.affiliateName);
        } else {
          this.couponCode.set('');
          this.couponDiscountRate.set(0);
          this.couponAffiliateName.set('');
        }
      })
    );
  }

  setFittCoinsRedemption(coins: number): void {
    const balance = this.userFittCoinsBalance();
    const safeCoins = Math.min(Math.max(0, coins), balance);
    this.fittCoinsToRedeem.set(safeCoins);
  }

  calculateFreight(zipCode: string): Observable<FreightQuote> {
    this.isCalculatingFreight.set(true);
    const cleanZip = zipCode.replace(/\D/g, '');
    const payload = {
      zipCode: cleanZip,
      subtotal: this.subtotal(),
      items: this.items().map(i => ({
        productId: i.product.id,
        quantity: i.quantity
      }))
    };

    return this.http.post<FreightQuote>(`${environment.apiUrl}/checkout/freight`, payload).pipe(
      tap({
        next: (quote) => {
          this.freightQuote.set(quote);
          this.isCalculatingFreight.set(false);
        },
        error: () => {
          this.isCalculatingFreight.set(false);
        }
      })
    );
  }

  submitCheckout(payload: CheckoutRequestPayload): Observable<CheckoutResult> {
    return this.http.post<CheckoutResult>(this.checkoutApi, payload);
  }
}
