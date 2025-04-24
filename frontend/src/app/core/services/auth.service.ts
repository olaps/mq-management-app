import { Injectable, PLATFORM_ID, Inject } from '@angular/core';
import { isPlatformBrowser } from '@angular/common';
import { HttpClient } from '@angular/common/http';
import { Observable, BehaviorSubject, throwError } from 'rxjs';
import { tap, catchError } from 'rxjs/operators';
import { environment } from '../../../environments/environment';
import { Router } from '@angular/router';

const AUTH_API = `${environment.apiUrl}/auth/`;
const TOKEN_KEY = 'auth-token';
const USER_KEY = 'auth-user';

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private currentUserSubject: BehaviorSubject<any>;
  public currentUser: Observable<any>;
  private isBrowser: boolean;

  constructor(
    private http: HttpClient,
    private router: Router,
    @Inject(PLATFORM_ID) platformId: Object
  ) {
    this.isBrowser = isPlatformBrowser(platformId);
    this.currentUserSubject = new BehaviorSubject<any>(this.getUserFromStorage());
    this.currentUser = this.currentUserSubject.asObservable();
  }

  login(username: string, password: string): Observable<any> {
    return this.http.post(AUTH_API + 'signin', {
      username,
      password
    }).pipe(
      tap((response: any) => {
        if (response?.token) {
          this.setToken(response.token);
          this.setUser(response);
          this.currentUserSubject.next(response);
        }
        return response;
      }),
      catchError(error => {
        return throwError(() => error);
      })
    );
  }

  register(username: string, email: string, password: string): Observable<any> {
    return this.http.post(AUTH_API + 'signup', {
      username,
      email,
      password
    }).pipe(
      catchError(error => {
        return throwError(() => error);
      })
    );
  }

  logout(): void {
    this.removeFromStorage(TOKEN_KEY);
    this.removeFromStorage(USER_KEY);
    this.currentUserSubject.next(null);
    this.router.navigate(['/login']);

  }

  public get currentUserValue(): any {
    return this.currentUserSubject.value;
  }

  isLoggedIn(): boolean {
    return !!this.getToken();
  }

  getToken(): string | null {
    return this.getFromStorage(TOKEN_KEY);
  }

  setToken(token: string): void {
    this.setInStorage(TOKEN_KEY, token);
  }

  setUser(user: any): void {
    this.setInStorage(USER_KEY, JSON.stringify(user));
  }

  getUserFromStorage(): any {
    const user = this.getFromStorage(USER_KEY);
    return user ? JSON.parse(user) : null;
  }

  // Méthodes sécurisées pour accéder au localStorage
  private getFromStorage(key: string): string | null {
    if (this.isBrowser) {
      return localStorage.getItem(key);
    }
    return null;
  }

  private setInStorage(key: string, value: string): void {
    if (this.isBrowser) {
      localStorage.setItem(key, value);
    }
  }

  private removeFromStorage(key: string): void {
    if (this.isBrowser) {
      localStorage.removeItem(key);
    }
  }

  hasRole(role: string): boolean {
    const user = this.currentUserValue;
    if (!user || !user.roles) {
      return false;
    }
    return user.roles.includes(role);
  }

  isAdmin(): boolean {
    return this.hasRole('ROLE_ADMIN');
  }

  isSupervisor(): boolean {
    return this.hasRole('ROLE_SUPERVISOR');
  }

  isUser(): boolean {
    return this.hasRole('ROLE_USER');
  }
}