-- V7: Informações Nutricionais Oficiais Anvisa RDC 429/2020 e IN 75/2020

ALTER TABLE products ADD COLUMN IF NOT EXISTS serving_size VARCHAR(100);
ALTER TABLE products ADD COLUMN IF NOT EXISTS calories_kcal INT;
ALTER TABLE products ADD COLUMN IF NOT EXISTS carbohydrates_g NUMERIC(5, 2);
ALTER TABLE products ADD COLUMN IF NOT EXISTS proteins_g NUMERIC(5, 2);
ALTER TABLE products ADD COLUMN IF NOT EXISTS total_fats_g NUMERIC(5, 2);
ALTER TABLE products ADD COLUMN IF NOT EXISTS saturated_fats_g NUMERIC(5, 2);
ALTER TABLE products ADD COLUMN IF NOT EXISTS dietary_fiber_g NUMERIC(5, 2);
ALTER TABLE products ADD COLUMN IF NOT EXISTS sodium_mg INT;
ALTER TABLE products ADD COLUMN IF NOT EXISTS key_actives VARCHAR(255);
ALTER TABLE products ADD COLUMN IF NOT EXISTS allergens VARCHAR(255);
ALTER TABLE products ADD COLUMN IF NOT EXISTS anvisa_notice VARCHAR(255);

-- Atualiza dados nutricionais de producao para suplementos do catalogo
UPDATE products
SET serving_size = '30g (1 dosador)',
    calories_kcal = 118,
    carbohydrates_g = 1.60,
    proteins_g = 26.50,
    total_fats_g = 0.70,
    saturated_fats_g = 0.20,
    dietary_fiber_g = 0.00,
    sodium_mg = 48,
    key_actives = 'BCAA 5.8g (Leucina 2.7g), Glutamina 4.5g, Glutationa natural',
    allergens = 'ALERGICOS: CONTEM DERIVADOS DE LEITE E SOJA. NAO CONTEM GLUTEN.',
    anvisa_notice = 'Produto dispensado de registro conforme RDC nº 240/2018 da Anvisa.'
WHERE sku = 'SUP-WHEY-ISO-01';

UPDATE products
SET serving_size = '3g (1 dosador raso)',
    calories_kcal = 0,
    carbohydrates_g = 0.00,
    proteins_g = 0.00,
    total_fats_g = 0.00,
    saturated_fats_g = 0.00,
    dietary_fiber_g = 0.00,
    sodium_mg = 0,
    key_actives = '100% Creatina Monohidratada Micronizada Creapure (3000mg)',
    allergens = 'NAO CONTEM ALERGENICOS. NAO CONTEM GLUTEN. VEGANO.',
    anvisa_notice = 'Produto dispensado de registro conforme RDC nº 240/2018 da Anvisa.'
WHERE sku = 'SUP-CREAT-CP-01';

UPDATE products
SET serving_size = '10g (2 dosadores)',
    calories_kcal = 22,
    carbohydrates_g = 1.20,
    proteins_g = 4.00,
    total_fats_g = 0.00,
    saturated_fats_g = 0.00,
    dietary_fiber_g = 0.00,
    sodium_mg = 65,
    key_actives = 'Beta Alanina 2000mg, Cafeina Anidra 400mg, L-Arginina 1500mg, Taurina 1000mg',
    allergens = 'PODE CONTER DERIVADOS DE SOJA E LEITE. CONTEM CAFEINA.',
    anvisa_notice = 'Produto dispensado de registro conforme RDC nº 240/2018 da Anvisa.'
WHERE sku = 'SUP-PRE-VEN-01';

UPDATE products
SET serving_size = '2 capsulas (2.8g)',
    calories_kcal = 18,
    carbohydrates_g = 0.00,
    proteins_g = 0.00,
    total_fats_g = 2.00,
    saturated_fats_g = 0.20,
    dietary_fiber_g = 0.00,
    sodium_mg = 0,
    key_actives = 'Acidos Graxos Omega 3 1320mg (EPA 660mg + DHA 440mg) com Vitamina E',
    allergens = 'ALERGICOS: CONTEM DERIVADOS DE PEIXE. NAO CONTEM GLUTEN.',
    anvisa_notice = 'Produto dispensado de registro conforme RDC nº 240/2018 da Anvisa.'
WHERE sku = 'SUP-OMEGA-01';

UPDATE products
SET serving_size = '1 tablete',
    calories_kcal = 0,
    carbohydrates_g = 0.00,
    proteins_g = 0.00,
    total_fats_g = 0.00,
    saturated_fats_g = 0.00,
    dietary_fiber_g = 0.00,
    sodium_mg = 0,
    key_actives = 'Complexo A-Z: Vitamina C 45mg, Zinco 7mg, Ferro 14mg, Magnesio 65mg, Vitamina D3 2000UI',
    allergens = 'NAO CONTEM GLUTEN.',
    anvisa_notice = 'Produto dispensado de registro conforme RDC nº 240/2018 da Anvisa.'
WHERE sku = 'SUP-MULTI-01';

UPDATE products
SET serving_size = '5g (1 dosador)',
    calories_kcal = 20,
    carbohydrates_g = 0.00,
    proteins_g = 5.00,
    total_fats_g = 0.00,
    saturated_fats_g = 0.00,
    dietary_fiber_g = 0.00,
    sodium_mg = 0,
    key_actives = '100% L-Glutamina Pura de Origem Vegetal e Fermentada (5000mg)',
    allergens = 'NAO CONTEM ALERGENICOS. NAO CONTEM GLUTEN.',
    anvisa_notice = 'Produto dispensado de registro conforme RDC nº 240/2018 da Anvisa.'
WHERE sku = 'SUP-GLUT-01';
