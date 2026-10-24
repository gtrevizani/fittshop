import { Injectable, signal, computed } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, tap } from 'rxjs';
import { environment } from '../../environments/environment';

export type UserRole = 'ROLE_CUSTOMER' | 'ROLE_SELLER' | 'ROLE_STOCKIST' | 'ROLE_CASHIER' | 'ROLE_MANAGER' | 'ROLE_ADMIN';

export interface AuthUser {
  userId: number;
  name: string;
  email: string;
  role: UserRole;
}

export interface AuthResponse {
  token: string;
  role: UserRole;
  name: string;
  email: string;
  userId: number;
}

export interface RegisterPayload {
  name: string;
  email: string;
  password: string;
  phone?: string;
  cpf?: string;
}

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private readonly TOKEN_KEY = 'fittshop_jwt_token';
  private readonly USER_KEY = 'fittshop_auth_user';

  currentUser = signal<AuthUser | null>(null);
  token = signal<string | null>(null);

  currentRole = computed<UserRole>(() => this.currentUser()?.role || 'ROLE_CUSTOMER');
  currentUserName = computed<string>(() => this.currentUser()?.name || 'Atleta Visitante');
  isAuthenticated = computed<boolean>(() => !!this.token());

  constructor(private http: HttpClient) {
    this.loadSessionFromStorage();
  }

  private loadSessionFromStorage(): void {
    try {
      if (typeof window !== 'undefined' && window.localStorage) {
        const savedToken = localStorage.getItem(this.TOKEN_KEY);
        const savedUser = localStorage.getItem(this.USER_KEY);
        if (savedToken && savedUser) {
          this.token.set(savedToken);
          this.currentUser.set(JSON.parse(savedUser));
        }
      }
    } catch (e) {
      console.warn('Erro ao restaurar sessão JWT:', e);
    }
  }

  login(email: string, password: string): Observable<AuthResponse> {
    return this.http.post<AuthResponse>(`${environment.apiUrl}/auth/login`, { email, password }).pipe(
      tap((res) => this.handleAuthSuccess(res))
    );
  }

  register(payload: RegisterPayload): Observable<AuthResponse> {
    return this.http.post<AuthResponse>(`${environment.apiUrl}/auth/register`, payload).pipe(
      tap((res) => this.handleAuthSuccess(res))
    );
  }

  private handleAuthSuccess(res: AuthResponse): void {
    const user: AuthUser = {
      userId: res.userId,
      name: res.name,
      email: res.email,
      role: res.role
    };

    this.token.set(res.token);
    this.currentUser.set(user);

    try {
      if (typeof window !== 'undefined' && window.localStorage) {
        localStorage.setItem(this.TOKEN_KEY, res.token);
        localStorage.setItem(this.USER_KEY, JSON.stringify(user));
      }
    } catch (e) {
      console.warn('Erro ao persistir sessão JWT no localStorage:', e);
    }
  }

  logout(): void {
    this.token.set(null);
    this.currentUser.set(null);
    try {
      if (typeof window !== 'undefined' && window.localStorage) {
        localStorage.removeItem(this.TOKEN_KEY);
        localStorage.removeItem(this.USER_KEY);
      }
    } catch (e) {
      console.warn('Erro ao limpar sessão:', e);
    }
  }

  setRole(role: UserRole): void {
    const current = this.currentUser();
    if (current) {
      const updated: AuthUser = { ...current, role };
      this.currentUser.set(updated);
      try {
        if (typeof window !== 'undefined' && window.localStorage) {
          localStorage.setItem(this.USER_KEY, JSON.stringify(updated));
        }
      } catch (e) {}
    } else {
      this.currentUser.set({
        userId: 0,
        name: 'Usuário ' + role.replace('ROLE_', ''),
        email: 'user@fittshop.com.br',
        role
      });
    }
  }

  isStaff(): boolean {
    return this.currentRole() !== 'ROLE_CUSTOMER';
  }
}
