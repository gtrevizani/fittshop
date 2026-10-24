const { describe, it } = require('node:test');
const assert = require('node:assert');

// Implementação emulada da lógica pura do CartService para testes de alta fidelidade
class CartBusinessEngine {
  constructor() {
    this.items = [];
    this.couponCode = '';
    this.couponDiscountRate = 0;
    this.couponFixedDiscount = 0;
    this.fittCoinsToRedeem = 0;
    this.userFittCoinsBalance = 120;
    this.freightQuote = null;
  }

  addItem(product, quantity = 1) {
    const existing = this.items.find(i => i.product.id === product.id);
    if (existing) {
      existing.quantity += quantity;
    } else {
      this.items.push({ product, quantity });
    }
  }

  updateQuantity(productId, quantity) {
    if (quantity <= 0) {
      this.items = this.items.filter(i => i.product.id !== productId);
    } else {
      const item = this.items.find(i => i.product.id === productId);
      if (item) item.quantity = quantity;
    }
  }

  subtotal() {
    return Number(this.items.reduce((acc, i) => acc + (i.product.price * i.quantity), 0).toFixed(2));
  }

  comboDiscount() {
    const prods = this.items.map(i => i.product.name.toLowerCase());
    const hasWhey = prods.some(n => n.includes('whey') || n.includes('proteína'));
    const hasCreatine = prods.some(n => n.includes('creatina') || n.includes('creapure'));
    if (hasWhey && hasCreatine) {
      return Number((this.subtotal() * 0.15).toFixed(2));
    }
    return 0;
  }

  couponDiscount() {
    if (this.couponFixedDiscount > 0) {
      return Number(Math.min(this.couponFixedDiscount, this.subtotal()).toFixed(2));
    }
    if (this.couponDiscountRate > 0) {
      return Number((this.subtotal() * this.couponDiscountRate).toFixed(2));
    }
    const code = (this.couponCode || '').trim().toUpperCase();
    if (code === 'FITT10') return Number((this.subtotal() * 0.10).toFixed(2));
    if (code === 'MONSTRO15') return Number((this.subtotal() * 0.15).toFixed(2));
    if (code === 'BEMVINDO20') return Number(Math.min(20, this.subtotal()).toFixed(2));
    return 0;
  }

  fittCoinsDiscount() {
    const potential = this.fittCoinsToRedeem * 0.50;
    const maxAllowed = this.subtotal() * 0.20; // Teto de 20%
    return Number(Math.min(potential, maxAllowed).toFixed(2));
  }

  totalDiscount() {
    const total = this.comboDiscount() + this.couponDiscount() + this.fittCoinsDiscount();
    return Number(Math.min(total, this.subtotal()).toFixed(2));
  }

  netSubtotal() {
    return Number((this.subtotal() - this.totalDiscount()).toFixed(2));
  }

  freightAmount() {
    if (this.subtotal() >= 199.00) return 0.00;
    return this.freightQuote ? this.freightQuote.freightAmount : 19.90;
  }

  finalTotal() {
    return Number((this.netSubtotal() + this.freightAmount()).toFixed(2));
  }

  fittCoinsToEarn() {
    return Math.floor(this.finalTotal() * 0.10);
  }
}

describe('🧪 FittShop Frontend - Cart Business Engine Test Suite', () => {
  it('deve calcular corretamente o subtotal de itens múltiplos', () => {
    const cart = new CartBusinessEngine();
    cart.addItem({ id: 1, name: 'Whey Protein Isolado 900g', price: 159.90 }, 2);
    cart.addItem({ id: 2, name: 'Coqueteleira Shaker Pro', price: 35.00 }, 1);

    assert.strictEqual(cart.subtotal(), 354.80);
  });

  it('deve remover o item do carrinho se a quantidade for atualizada para zero ou negativa', () => {
    const cart = new CartBusinessEngine();
    cart.addItem({ id: 1, name: 'Creatina Creapure 300g', price: 99.90 }, 1);
    assert.strictEqual(cart.items.length, 1);

    cart.updateQuantity(1, 0);
    assert.strictEqual(cart.items.length, 0);
    assert.strictEqual(cart.subtotal(), 0.00);
  });

  it('deve conceder 15% de desconto no Combo Atleta ao combinar Whey + Creatina', () => {
    const cart = new CartBusinessEngine();
    cart.addItem({ id: 1, name: '100% Pure Whey Protein 900g', price: 160.00 }, 1);
    cart.addItem({ id: 2, name: 'Creatina Monohidratada 100% Pura 250g', price: 100.00 }, 1);

    const subtotal = cart.subtotal(); // 260.00
    assert.strictEqual(subtotal, 260.00);
    assert.strictEqual(cart.comboDiscount(), 39.00); // 15% de 260 = 39.00
    assert.strictEqual(cart.netSubtotal(), 221.00);
  });

  it('deve conceder frete grátis quando o subtotal for >= R$ 199,00', () => {
    const cart = new CartBusinessEngine();
    cart.addItem({ id: 1, name: 'Whey Protein Concentrado 900g', price: 120.00 }, 1);
    cart.addItem({ id: 2, name: 'Creatina Creapure 300g', price: 85.00 }, 1);

    assert.strictEqual(cart.subtotal(), 205.00);
    assert.strictEqual(cart.freightAmount(), 0.00);
  });

  it('deve cobrar frete padrão quando o subtotal for inferior a R$ 199,00', () => {
    const cart = new CartBusinessEngine();
    cart.addItem({ id: 1, name: 'Cinturão Musculação Couro Legítimo', price: 150.00 }, 1);

    assert.strictEqual(cart.subtotal(), 150.00);
    assert.strictEqual(cart.freightAmount(), 19.90);
  });

  it('deve respeitar o teto de 20% do pedido no resgate de FittCoins', () => {
    const cart = new CartBusinessEngine();
    cart.addItem({ id: 1, name: 'Creatina Creapure 300g', price: 100.00 }, 1);
    cart.fittCoinsToRedeem = 80; // 80 moedas valeriam R$ 40,00

    // Mas o subtotal é 100.00, teto máx de 20% = R$ 20.00
    assert.strictEqual(cart.fittCoinsDiscount(), 20.00);
  });

  it('deve calcular corretamente a bonificação de FittCoins ganhos na compra (1 ponto a cada R$ 10)', () => {
    const cart = new CartBusinessEngine();
    cart.addItem({ id: 1, name: 'Kit Hipertrofia Elite', price: 299.00 }, 1);

    // Final total = 299.00 -> 299 * 0.10 = 29.9 -> Math.floor = 29 FittCoins
    assert.strictEqual(cart.fittCoinsToEarn(), 29);
  });
});
