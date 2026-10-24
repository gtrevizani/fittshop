const { describe, it } = require('node:test');
const assert = require('node:assert');

// Motor NutriFit IA puro espelhado no backend
class NutriFitEngine {
  static calculate(profile) {
    const { goal, weightKg, heightMeters, weeklyWorkouts } = profile;
    const bmi = Number((weightKg / (heightMeters * heightMeters)).toFixed(1));

    let proteinMultiplier = 1.6;
    let waterMlPerKg = 35;
    let strategy = '';

    switch (goal) {
      case 'HIPERTROFIA':
        proteinMultiplier = weeklyWorkouts >= 5 ? 2.2 : 2.0;
        waterMlPerKg = 45;
        strategy = 'Superávit Calórico Limpo & Balanço Nitrogenado Positivo';
        break;
      case 'DEFINICAO':
        proteinMultiplier = 2.4; // Preservação de massa magra em déficit calórico
        waterMlPerKg = 50;
        strategy = 'Déficit Calórico Moderado & Termogênese Acelerada';
        break;
      case 'FORCA_POWERLIFTING':
        proteinMultiplier = 2.0;
        waterMlPerKg = 45;
        strategy = 'Fosfocreatina Saturada & Cargas Neurais Máximas';
        break;
      case 'RESISTENCIA_CARDIO':
        proteinMultiplier = 1.6;
        waterMlPerKg = 55;
        strategy = 'Glicogênio Pleno & Reposição Eletrolítica';
        break;
      default:
        proteinMultiplier = 1.8;
    }

    const dailyProteinGrams = Math.round(weightKg * proteinMultiplier);
    const dailyCreatineGrams = Number((weightKg * 0.07).toFixed(1)); // Padrão ouro 0.07g/kg
    const dailyWaterLiters = Number(((weightKg * waterMlPerKg) / 1000).toFixed(1));

    return {
      bmi,
      dailyProteinGrams,
      dailyCreatineGrams,
      dailyWaterLiters,
      strategy
    };
  }

  static calculateComboPricing(products) {
    const regularTotal = Number(products.reduce((acc, p) => acc + p.price, 0).toFixed(2));
    const discountedTotal = Number((regularTotal * 0.85).toFixed(2)); // 15% OFF
    const savings = Number((regularTotal - discountedTotal).toFixed(2));

    return {
      regularTotal,
      discountedTotal,
      savings
    };
  }
}

describe('🧪 FittShop Frontend - NutriFit Recommendation Engine Test Suite', () => {
  it('deve calcular IMC e dosagem de creatina padrão ouro (0.07g/kg)', () => {
    const profile = {
      goal: 'HIPERTROFIA',
      weightKg: 80,
      heightMeters: 1.80,
      weeklyWorkouts: 5
    };

    const res = NutriFitEngine.calculate(profile);
    assert.strictEqual(res.bmi, 24.7);
    assert.strictEqual(res.dailyCreatineGrams, 5.6); // 80 * 0.07 = 5.6g
  });

  it('deve aplicar 2.2g/kg de proteína para hipertrofia com 5 ou mais treinos semanais', () => {
    const profile = {
      goal: 'HIPERTROFIA',
      weightKg: 75,
      heightMeters: 1.75,
      weeklyWorkouts: 6
    };

    const res = NutriFitEngine.calculate(profile);
    assert.strictEqual(res.dailyProteinGrams, 165); // 75 * 2.2 = 165g
  });

  it('deve elevar o aporte proteico para 2.4g/kg em fase de definição muscular para evitar catabolismo', () => {
    const profile = {
      goal: 'DEFINICAO',
      weightKg: 70,
      heightMeters: 1.70,
      weeklyWorkouts: 5
    };

    const res = NutriFitEngine.calculate(profile);
    assert.strictEqual(res.dailyProteinGrams, 168); // 70 * 2.4 = 168g
  });

  it('deve calcular precificação com desconto de combo de 15% para produtos recomendados', () => {
    const stack = [
      { id: 1, name: 'Whey Protein Isolado', price: 160.00 },
      { id: 2, name: 'Creatina Creapure', price: 100.00 },
      { id: 3, name: 'Pré-Treino Venom', price: 140.00 }
    ];

    const pricing = NutriFitEngine.calculateComboPricing(stack);
    assert.strictEqual(pricing.regularTotal, 400.00);
    assert.strictEqual(pricing.discountedTotal, 340.00); // 400 * 0.85 = 340.00
    assert.strictEqual(pricing.savings, 60.00); // 400 - 340 = 60.00
  });
});
