import { Injectable } from '@angular/core';
import { CanActivate, ActivatedRouteSnapshot, RouterStateSnapshot, Router } from '@angular/router';
import { AuthService } from '../services/auth.service';

@Injectable({
  providedIn: 'root'
})
export class AuthGuard implements CanActivate {
  constructor(private authService: AuthService, private router: Router) {}

  canActivate(route: ActivatedRouteSnapshot, state: RouterStateSnapshot): boolean {
    if (this.authService.isLoggedIn()) {
      // Vérifier si l'utilisateur a le rôle requis
      const requiredRoles = route.data['roles'] as Array<string>;
      if (requiredRoles) {
        const hasRole = requiredRoles.some(role => this.authService.hasRole(role));
        if (!hasRole) {
          this.router.navigate(['/login'], { queryParams: { returnUrl: state.url }});
          return false;
        }
      }
      return true;
    }

    // Non connecté, rediriger vers la page de connexion
    this.router.navigate(['/login'], { queryParams: { returnUrl: state.url }});
    return false;
  }
}