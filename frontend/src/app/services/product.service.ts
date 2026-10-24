import { Injectable, signal } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, of } from 'rxjs';
import { catchError, tap } from 'rxjs/operators';
import { Product } from '../models/product.model';

@Injectable({
  providedIn: 'root'
})
export class ProductService {
  private apiUrl = 'http://localhost:8080/api/products';

  // Seed fallback catalog for out-of-the-box UI responsiveness
  private initialProducts: Product[] = [
    {
      id: 1,
      sku: 'SUP-WHEY-100',
      name: '100% Whey Protein Concentrado 900g - Baunilha Nobre',
      description: 'Proteína de altíssimo valor biológico, 24g de proteína por dose, 5.5g de BCAAs e matéria-prima importada 100% filtrada.',
      price: 139.90,
      category: 'Suplementos',
      imageUrl: 'https://images.unsplash.com/photo-1579722821273-0f6c7d44362f?auto=format&fit=crop&w=600&q=80',
      nutritionalInfo: 'Dose 30g: 24g Proteína, 2.1g Carbo, 1.5g Gorduras, 5.4g BCAA, 4.3g Glutamina.',
      targetGoal: 'HIPERTROFIA',
      stockQuantity: 50,
      minStockThreshold: 5,
      batchCode: 'LOTE-WH-2026A',
      expirationDate: '2027-10-30',
      weightKg: 0.9
    },
    {
      id: 2,
      sku: 'SUP-CREA-PURE',
      name: 'Creatina Creapure® 300g 100% Pura Micronizada',
      description: 'Selo de pureza Creapure® alemã. Aumento explosivo de força muscular, potência e hidratação celular intra-miofibrilar.',
      price: 119.90,
      category: 'Suplementos',
      imageUrl: 'https://images.unsplash.com/photo-1593095948071-474c5cc2989d?auto=format&fit=crop&w=600&q=80',
      nutritionalInfo: 'Dose 3g: 100% Creatina Monohidratada Creapure® pura. 0 calorias, 0 sódio.',
      targetGoal: 'HIPERTROFIA',
      stockQuantity: 80,
      minStockThreshold: 10,
      batchCode: 'LOTE-CR-2026B',
      expirationDate: '2028-04-15',
      weightKg: 0.3
    },
    {
      id: 3,
      sku: 'SUP-PRE-VENOM',
      name: 'Pré-Treino Venom Extreme 300g - Maçã Verde',
      description: 'Fórmula insana com 400mg de Cafeína Anidra, 3000mg de Beta-Alanina, 1000mg de Taurina e Tirosina para foco cirúrgico.',
      price: 149.90,
      category: 'Suplementos',
      imageUrl: 'https://images.unsplash.com/photo-1546483875-ad9014c88eba?auto=format&fit=crop&w=600&q=80',
      nutritionalInfo: 'Dose 10g: 400mg Cafeína, 3000mg Beta-Alanina, 1000mg Arginina, 1000mg Taurina.',
      targetGoal: 'FORCA_POWERLIFTING',
      stockQuantity: 35,
      minStockThreshold: 5,
      batchCode: 'LOTE-VN-2026C',
      expirationDate: '2027-08-20',
      weightKg: 0.35
    },
    {
      id: 4,
      sku: 'SUP-ISO-ZERO',
      name: 'Whey Isolate Zero Carbo 900g - Chocolate Belga',
      description: 'Proteína isolada com microfiltração por fluxo cruzado (CFM). Zero lactose, zero glúten e absorção ultrarrápida.',
      price: 219.90,
      category: 'Suplementos',
      imageUrl: 'https://images.unsplash.com/photo-1584308666744-24d5c474f2ae?auto=format&fit=crop&w=600&q=80',
      nutritionalInfo: 'Dose 30g: 27g Proteína Isolada, 0g Carbo, 0g Gordura, 6.2g BCAA.',
      targetGoal: 'DEFINICAO',
      stockQuantity: 40,
      minStockThreshold: 5,
      batchCode: 'LOTE-IZ-2026D',
      expirationDate: '2027-12-10',
      weightKg: 0.9
    },
    {
      id: 5,
      sku: 'ACE-BELT-10MM',
      name: 'Cinturão de Couro Legítimo 10mm Powerlifting',
      description: 'Fabricado em couro bovino selecionado com fivela dupla em aço inox. Estabilidade lombar e pressão intra-abdominal máxima.',
      price: 189.90,
      category: 'Acessórios',
      imageUrl: 'https://images.unsplash.com/photo-1517838277536-f5f99be501cd?auto=format&fit=crop&w=600&q=80',
      nutritionalInfo: 'Couro 100% legítimo com 10mm de espessura. Certificado para agachamento e terra.',
      targetGoal: 'FORCA_POWERLIFTING',
      stockQuantity: 25,
      minStockThreshold: 3,
      batchCode: 'LOTE-EQ-01',
      expirationDate: '2035-01-01',
      weightKg: 1.2
    },
    {
      id: 6,
      sku: 'ROU-CAM-OVER',
      name: 'Camiseta Oversized FittShop Heavy Duty - Preta',
      description: 'Modelagem oversized streetwear/gym, 100% algodão peruano fio 30.1 com costura reforçada ombro a ombro.',
      price: 89.90,
      category: 'Roupas',
      imageUrl: 'https://images.unsplash.com/photo-1521572267360-ee0c2909d518?auto=format&fit=crop&w=600&q=80',
      nutritionalInfo: 'Algodão 100% penteado peruano 220g/m². Caimento folgado ideal para treino de costas e peito.',
      targetGoal: 'HIPERTROFIA',
      stockQuantity: 60,
      minStockThreshold: 10,
      batchCode: 'LOTE-RP-02',
      expirationDate: '2030-01-01',
      weightKg: 0.25
    },
    {
      id: 7,
      sku: 'ACE-SHAKER-INOX',
      name: 'Coqueteleira Shaker Térmica Inox 750ml com Blender',
      description: 'Mantém sua bebida gelada por até 24 horas. Fecho antivazamento de clique duplo e escala medidora interna.',
      price: 69.90,
      category: 'Acessórios',
      imageUrl: 'https://images.unsplash.com/photo-1544816155-12df9643f363?auto=format&fit=crop&w=600&q=80',
      nutritionalInfo: 'Aço Inoxidável 304 livre de BPA e toxinas. Isolamento a vácuo com parede dupla.',
      targetGoal: 'RESISTENCIA_CARDIO',
      stockQuantity: 100,
      minStockThreshold: 15,
      batchCode: 'LOTE-EQ-03',
      expirationDate: '2035-01-01',
      weightKg: 0.4
    },
    {
      id: 8,
      sku: 'COMBO-HIPER-PRO',
      name: 'Combo Hipertrofia Máxima (Whey 100% + Creatina Creapure + Shaker)',
      description: 'O kit definitivo para explosão muscular e recomposição corporal com desconto promocional integrado de 15%.',
      price: 279.90,
      category: 'Combos',
      imageUrl: 'https://images.unsplash.com/photo-1517838277536-f5f99be501cd?auto=format&fit=crop&w=600&q=80',
      nutritionalInfo: 'Contém: 1x Whey 900g + 1x Creatina 300g Creapure® + 1x Shaker Inox Térmico 750ml.',
      targetGoal: 'HIPERTROFIA',
      stockQuantity: 30,
      minStockThreshold: 5,
      batchCode: 'LOTE-CB-01',
      expirationDate: '2027-10-30',
      weightKg: 1.6
    }
  ];

  productsSignal = signal<Product[]>(this.initialProducts);

  constructor(private http: HttpClient) {
    this.fetchProducts();
  }

  fetchProducts(): void {
    this.http.get<Product[]>(this.apiUrl).pipe(
      tap(prods => {
        if (prods && prods.length > 0) {
          this.productsSignal.set(prods);
        }
      }),
      catchError(() => {
        // Fallback gracefully to seed products
        return of(this.initialProducts);
      })
    ).subscribe();
  }

  getProducts(): Product[] {
    return this.productsSignal();
  }

  adjustStockLocal(productId: number, delta: number): void {
    this.productsSignal.update(items =>
      items.map(p => p.id === productId ? { ...p, stockQuantity: Math.max(0, p.stockQuantity + delta) } : p)
    );
  }
}
