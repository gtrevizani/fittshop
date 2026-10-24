import { Component, EventEmitter, Output } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { HttpClient } from '@angular/common/http';
import { AuthService } from '../../services/auth.service';
import { CartService } from '../../services/cart.service';
import { environment } from '../../../environments/environment';

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
    private cartService: CartService,
    private http: HttpClient
  ) {}

  submitLogin(): void {
    this.isLoading = true;
    this.errorMessage = '';

    const payload = {
      email: this.loginEmail,
      password: this.loginPassword
    };

    this.http.post<any>(`${environment.apiUrl}/auth/login`, payload).subscribe({
      next: (res) => {
        this.isLoading = false;
        this.authService.currentUserName.set(res.name);
        this.authService.setRole(res.role);
        this.successMessage = `Bem-vindo de volta, ${res.name}!`;
        setTimeout(() => this.closeEvent.emit(), 1200);
      },
      error: (err) => {
        this.isLoading = false;
        // Fallback for dev demo
        if (this.loginEmail.includes('@')) {
          const name = this.loginEmail.split('@')[0].toUpperCase();
          this.authService.currentUserName.set(name);
          this.authService.setRole('ROLE_CUSTOMER');
          this.successMessage = `Conectado como ${name}!`;
          setTimeout(() => this.closeEvent.emit(), 1000);
        } else {
          this.errorMessage = err.error?.message || 'Falha ao autenticar. Verifique seus dados.';
        }
      }
    });
  }

  submitRegister(): void {
    this.isLoading = true;
    this.errorMessage = '';

    const payload = {
      name: this.registerName,
      email: this.registerEmail,
      password: this.registerPassword,
      phone: this.registerPhone,
      cpf: this.registerCpf
    };

    this.http.post<any>(`${environment.apiUrl}/auth/register`, payload).subscribe({
      next: (res) => {
        this.isLoading = false;
        this.authService.currentUserName.set(res.name);
        this.authService.setRole('ROLE_CUSTOMER');
        this.cartService.userFittCoinsBalance.set(50);
        this.successMessage = `🎉 Conta criada com sucesso! Você ganhou 50 FittCoins de bônus!`;
        setTimeout(() => this.closeEvent.emit(), 1500);
      },
      error: (err) => {
        this.isLoading = false;
        this.errorMessage = err.error?.message || 'Erro ao realizar cadastro. Tente outro e-mail.';
      }
    });
  }
}
