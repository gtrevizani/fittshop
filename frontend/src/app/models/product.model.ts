export interface Product {
  id: number;
  sku: string;
  name: string;
  description: string;
  price: number;
  category: 'Suplementos' | 'Roupas' | 'Acessórios' | 'Combos';
  imageUrl: string;
  nutritionalInfo: string;
  targetGoal: 'HIPERTROFIA' | 'DEFINICAO' | 'FORCA_POWERLIFTING' | 'RESISTENCIA_CARDIO';
  stockQuantity: number;
  minStockThreshold: number;
  batchCode?: string;
  expirationDate?: string;
  weightKg?: number;
}
