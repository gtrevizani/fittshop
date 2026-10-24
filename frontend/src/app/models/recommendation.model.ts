import { Product } from './product.model';

export interface AthleteProfilePayload {
  goal: 'HIPERTROFIA' | 'DEFINICAO' | 'FORCA_POWERLIFTING' | 'RESISTENCIA_CARDIO';
  weightKg: number;
  heightMeters: number;
  weeklyWorkouts: number;
  experienceLevel: string;
}

export interface RecommendationResult {
  goal: string;
  dailyProteinGrams: number;
  dailyCreatineGrams: number;
  estimatedWaterLiters: number;
  rationale: string;
  comboDiscountPercentage: number;
  recommendedProducts: Product[];
  regularTotalPrice: number;
  comboDiscountedPrice: number;
  totalSavings: number;
}
