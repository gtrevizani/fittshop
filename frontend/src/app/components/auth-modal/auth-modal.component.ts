import { Component, EventEmitter, HostListener, Output } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { AuthService } from '../../services/auth.service';
import { CartService } from '../../services/cart.service';
import { ToastService } from '../../services/toast.service';

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
  loginEmail = '';
  loginPassword = '';

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
    private toastService: ToastService
  ) {}

  @HostListener('document:keydown.escape')
  onEscape(): void {
    this.closeEvent.emit();
  }

  submitLogin(): void {
    this.isLoading = true;
    this.errorMessage = '';
    this.successMessage = '';

    this.authService.login(this.loginEmail, this.loginPassword).subscribe({
      next: (res) => {
        this.isLoading = false;
        this.successMessage = `Bem-vindo de volta, ${res.name}!`;
        this.toastService.success(`🏋️ Bem-vindo ao FittShop, ${res.name}!`);
        setTimeout(() => this.closeEvent.emit(), 600);
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

    if (this.registerCpf && this.registerCpf.trim()) {
      if (!this.validateCpf(this.registerCpf)) {
        this.isLoading = false;
        this.errorMessage = 'CPF inválido: os dígitos verificadores informados não conferem com a Receita Federal.';
        return;
      }
    }

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
        this.toastService.success(`🎉 Atleta ${res.name} cadastrado com sucesso! +50 FittCoins creditados.`);
        setTimeout(() => this.closeEvent.emit(), 800);
      },
      error: (err) => {
        this.isLoading = false;
        this.errorMessage = err.error?.message || err.error?.error || 'Erro ao realizar cadastro. Tente outro e-mail.';
      }
    });
  }

  validateCpf(cpf: string): boolean {
    const clean = cpf.replace(/\D/g, '');
    if (clean.length !== 11 || /^(\d)\1{10}$/.test(clean)) return false;
    let sum = 0, rest;
    for (let i = 1; i <= 9; i++) sum += parseInt(clean.substring(i - 1, i)) * (11 - i);
    rest = (sum * 10) % 11;
    if (rest === 10 || rest === 11) rest = 0;
    if (rest !== parseInt(clean.substring(9, 10))) return false;
    sum = 0;
    for (let i = 1; i <= 10; i++) sum += parseInt(clean.substring(i - 1, i)) * (12 - i);
    rest = (sum * 10) % 11;
    if (rest === 10 || rest === 11) rest = 0;
    return rest === parseInt(clean.substring(10, 11));
  }
}
