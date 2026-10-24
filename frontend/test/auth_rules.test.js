const { describe, it } = require('node:test');
const assert = require('node:assert');

class AthleteAuthEngine {
  static validateCpf(cpf) {
    if (!cpf) return false;
    const clean = cpf.replace(/\D/g, '');
    if (clean.length !== 11 || /^(\d)\1{10}$/.test(clean)) return false;

    let sum = 0;
    for (let i = 1; i <= 9; i++) {
      sum += parseInt(clean.substring(i - 1, i)) * (11 - i);
    }
    let rest = (sum * 10) % 11;
    if (rest === 10 || rest === 11) rest = 0;
    if (rest !== parseInt(clean.substring(9, 10))) return false;

    sum = 0;
    for (let i = 1; i <= 10; i++) {
      sum += parseInt(clean.substring(i - 1, i)) * (12 - i);
    }
    rest = (sum * 10) % 11;
    if (rest === 10 || rest === 11) rest = 0;
    if (rest !== parseInt(clean.substring(10, 11))) return false;

    return true;
  }

  static isStaffRole(role) {
    if (!role) return false;
    const cleanRole = role.toUpperCase();
    return cleanRole === 'ROLE_ADMIN' || cleanRole === 'ROLE_ESTOQUISTA' || cleanRole === 'ROLE_CAIXA';
  }

  static canManageStock(role) {
    if (!role) return false;
    const cleanRole = role.toUpperCase();
    return cleanRole === 'ROLE_ADMIN' || cleanRole === 'ROLE_ESTOQUISTA';
  }

  static canApprovePayments(role) {
    if (!role) return false;
    const cleanRole = role.toUpperCase();
    return cleanRole === 'ROLE_ADMIN' || cleanRole === 'ROLE_CAIXA';
  }
}

describe('🧪 FittShop Frontend - Auth & RBAC Rules Test Suite', () => {
  it('deve validar positivamente CPFs válidos com dígitos verificadores corretos', () => {
    // CPFs válidos padrão de teste Receita Federal
    assert.strictEqual(AthleteAuthEngine.validateCpf('52998224725'), true);
    assert.strictEqual(AthleteAuthEngine.validateCpf('529.982.247-25'), true);
    assert.strictEqual(AthleteAuthEngine.validateCpf('11144477735'), true);
  });

  it('deve rejeitar CPFs com sequências de dígitos idênticos', () => {
    assert.strictEqual(AthleteAuthEngine.validateCpf('000.000.000-00'), false);
    assert.strictEqual(AthleteAuthEngine.validateCpf('111.111.111-11'), false);
    assert.strictEqual(AthleteAuthEngine.validateCpf('999.999.999-99'), false);
  });

  it('deve rejeitar CPFs com tamanho incompleto ou dígitos verificadores corrompidos', () => {
    assert.strictEqual(AthleteAuthEngine.validateCpf('123.456.789-00'), false);
    assert.strictEqual(AthleteAuthEngine.validateCpf('1234567'), false);
    assert.strictEqual(AthleteAuthEngine.validateCpf(''), false);
    assert.strictEqual(AthleteAuthEngine.validateCpf(null), false);
  });

  it('deve autorizar acesso ao Backoffice exclusivamente para funções Staff', () => {
    assert.strictEqual(AthleteAuthEngine.isStaffRole('ROLE_ADMIN'), true);
    assert.strictEqual(AthleteAuthEngine.isStaffRole('ROLE_ESTOQUISTA'), true);
    assert.strictEqual(AthleteAuthEngine.isStaffRole('ROLE_CAIXA'), true);
    assert.strictEqual(AthleteAuthEngine.isStaffRole('ROLE_ATLETA'), false);
    assert.strictEqual(AthleteAuthEngine.isStaffRole('ROLE_VISITOR'), false);
    assert.strictEqual(AthleteAuthEngine.isStaffRole(null), false);
  });

  it('deve restringir a gestão de estoque para Admin e Estoquista', () => {
    assert.strictEqual(AthleteAuthEngine.canManageStock('ROLE_ADMIN'), true);
    assert.strictEqual(AthleteAuthEngine.canManageStock('ROLE_ESTOQUISTA'), true);
    assert.strictEqual(AthleteAuthEngine.canManageStock('ROLE_CAIXA'), false);
    assert.strictEqual(AthleteAuthEngine.canManageStock('ROLE_ATLETA'), false);
  });
});
