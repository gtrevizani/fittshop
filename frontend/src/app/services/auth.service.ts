import { Injectable, signal } from '@angular/core';

export type UserRole = 'ROLE_CUSTOMER' | 'ROLE_SELLER' | 'ROLE_STOCKIST' | 'ROLE_CASHIER' | 'ROLE_MANAGER';

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  currentRole = signal<UserRole>('ROLE_CUSTOMER');
  currentUserName = signal<string>('Lucas Atleta');

  setRole(role: UserRole): void {
    this.currentRole.set(role);
    switch (role) {
      case 'ROLE_CUSTOMER':
        this.currentUserName.set('Lucas Atleta');
        break;
      case 'ROLE_SELLER':
        this.currentUserName.set('Camila Consultora (Vendas)');
        break;
      case 'ROLE_STOCKIST':
        this.currentUserName.set('Carlos Estoquista (Galpão)');
        break;
      case 'ROLE_CASHIER':
        this.currentUserName.set('Mariana Caixa (PDV)');
        break;
      case 'ROLE_MANAGER':
        this.currentUserName.set('Roberto Gerente de Loja');
        break;
    }
  }

  isStaff(): boolean {
    return this.currentRole() !== 'ROLE_CUSTOMER';
  }
}
