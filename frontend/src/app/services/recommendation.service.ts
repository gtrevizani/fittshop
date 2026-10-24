import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, of } from 'rxjs';
import { catchError } from 'rxjs/operators';
import { AthleteProfilePayload, RecommendationResult } from '../models/recommendation.model';
import { ProductService } from './product.service';

@Injectable({
  providedIn: 'root'
})
export class RecommendationService {
  private apiUrl = 'http://localhost:8080/api/recommendations/calculate';

  constructor(private http: HttpClient, private productService: ProductService) {}

  calculate(profile: AthleteProfilePayload): Observable<RecommendationResult> {
    return this.http.post<RecommendationResult>(this.apiUrl, profile).pipe(
      catchError(() => {
        // Fallback algorítmico espelhado do backend
        const allProducts = this.productService.getProducts();
        let targetGoalProducts = allProducts.filter(p => p.targetGoal === profile.goal);
        if (targetGoalProducts.length === 0) {
          targetGoalProducts = allProducts.slice(0, 3);
        }

        const proteinPerKg = profile.goal === 'DEFINICAO' ? 2.2 : (profile.goal === 'HIPERTROFIA' ? 2.0 : 1.8);
        const dailyProtein = Math.round(profile.weightKg * proteinPerKg);
        const waterLiters = Number((profile.weightKg * 0.045).toFixed(1));

        const regularTotal = targetGoalProducts.reduce((sum, p) => sum + p.price, 0);
        const comboDiscounted = Number((regularTotal * 0.85).toFixed(2));
        const savings = Number((regularTotal - comboDiscounted).toFixed(2));

        const result: RecommendationResult = {
          goal: profile.goal,
          dailyProteinGrams: dailyProtein,
          dailyCreatineGrams: 5.0,
          estimatedWaterLiters: waterLiters,
          rationale: `Para seu peso de ${profile.weightKg}kg e objetivo de ${profile.goal}, o balanço nitrogenado ideal exige ${dailyProtein}g diárias de proteína de rápida absorção somadas a 5g de creatina para saturação mitocondrial.`,
          comboDiscountPercentage: 0.15,
          recommendedProducts: targetGoalProducts,
          regularTotalPrice: Number(regularTotal.toFixed(2)),
          comboDiscountedPrice: comboDiscounted,
          totalSavings: savings
        };

        return of(result);
      })
    );
  }
}
