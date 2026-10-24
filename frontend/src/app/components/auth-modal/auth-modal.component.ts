import { Component, EventEmitter, Output } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { AuthService } from '../../services/auth.service';
import { CartService } from '../../services/cart.service';

@Component({
  selector: 'app-auth-modal',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './auth-modal.component.html',
  styleUrls: ['./auth-modal.component.css']
})
export class AuthModalComponent {
  @Output() closeEvent = new EventEmitter<void>();

  mode: 'LOGIN' | 'REGISTER' = 'LOGIN';

  // Login fields
  loginEmail = 'atleta@fittshop.com.br';
  loginPassword = 'password123';

  // Register fields
  registerName = '';
  registerEmail = '';
  registerPassword = '';
  registerPhone = '';
  registerCpf = '';

  errorMessage = '';
  successMessage = '';
  isLoading = false;

  constructor(
    public authService: AuthService,
    private cartService: CartService
  ) {}

  submitLogin(): void {
    this.isLoading = true;
    this.errorMessage = '';
    this.successMessage = '';

    this.authService.login(this.loginEmail, this.loginPassword).subscribe({
      next: (res) => {
        this.isLoading = false;
        this.successMessage = `Bem-vindo de volta, ${res.name}!`;
        setTimeout(() => this.closeEvent.emit(), 1000);
      },
      error: (err) => {
        this.isLoading = false;
        this.errorMessage = err.error?.message || err.error?.error || 'Credenciais inválidas. Verifique seu e-mail e senha.';
      }
    });
  }

  submitRegister(): void {
    this.isLoading = true;
    this.errorMessage = '';
    this.successMessage = '';

    const payload = {
      name: this.registerName,
      email: this.registerEmail,
      password: this.registerPassword,
      phone: this.registerPhone || undefined,
      cpf: this.registerCpf || undefined
    };

    this.authService.register(payload).subscribe({
      next: (res) => {
        this.isLoading = false;
        this.cartService.userFittCoinsBalance.set(50);
        this.successMessage = `🎉 Conta criada com sucesso! Você ganhou 50 FittCoins de bônus!`;
        setTimeout(() => this.closeEvent.emit(), 1200);
      },
      error: (err) => {
        this.isLoading = false;
        this.errorMessage = err.error?.message || err.error?.error || 'Erro ao realizar cadastro. Tente outro e-mail.';
      }
    });
  }
}
