import { Injectable, signal } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, of } from 'rxjs';
import { catchError, tap } from 'rxjs/operators';
import { Product } from '../models/product.model';
import { environment } from '../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class ProductService {
  private apiUrl = `${environment.apiUrl}/products`;
  private inventoryApiUrl = `${environment.apiUrl}/inventory`;

  productsSignal = signal<Product[]>([]);
  isLoading = signal<boolean>(false);
  loadError = signal<string | null>(null);

  constructor(private http: HttpClient) {
    this.fetchProducts();
  }

  fetchProducts(): void {
    this.isLoading.set(true);
    this.loadError.set(null);
    this.http.get<Product[]>(this.apiUrl).pipe(
      tap(prods => {
        this.productsSignal.set(prods || []);
        this.isLoading.set(false);
      }),
      catchError(err => {
        console.error('Falha ao obter catálogo de produtos da API:', err);
        this.loadError.set('Não foi possível carregar os produtos do servidor.');
        this.isLoading.set(false);
        this.productsSignal.set([]);
        return of([]);
      })
    ).subscribe();
  }

  getProducts(): Product[] {
    return this.productsSignal();
  }

  adjustStock(productId: number, quantityDelta: number, batchCode?: string, expirationDate?: string): Observable<Product> {
    const payload = {
      productId,
      quantityDelta,
      operationType: quantityDelta > 0 ? 'REPOSICAO' : 'BAIXA_MANUAL',
      batchCode: batchCode || 'LOTE-REMOTE',
      expirationDate: expirationDate || '2028-12-31'
    };

    return this.http.post<Product>(`${this.inventoryApiUrl}/adjust`, payload).pipe(
      tap(updated => {
        this.productsSignal.update(items =>
          items.map(p => p.id === productId ? { ...p, stockQuantity: updated.stockQuantity } : p)
        );
      })
    );
  }
}
