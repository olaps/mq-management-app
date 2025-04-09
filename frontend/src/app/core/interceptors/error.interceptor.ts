import { Injectable } from '@angular/core';
import {
  HttpRequest,
  HttpHandler,
  HttpEvent,
  HttpInterceptor,
  HttpErrorResponse
} from '@angular/common/http';
import { Observable, throwError } from 'rxjs';
import { catchError } from 'rxjs/operators';
import { MatSnackBar } from '@angular/material/snack-bar';

@Injectable()
export class ErrorInterceptor implements HttpInterceptor {

  constructor(private snackBar: MatSnackBar) {}

  intercept(request: HttpRequest<unknown>, next: HttpHandler): Observable<HttpEvent<unknown>> {
    return next.handle(request).pipe(
      catchError((error: HttpErrorResponse) => {
        let errorMessage = 'Une erreur est survenue';
        
        if (error.error instanceof ErrorEvent) {
          // Erreur côté client
          errorMessage = `Erreur: ${error.error.message}`;
        } else if (error.status) {
          // Erreur retournée par le backend
          switch (error.status) {
            case 400:
              errorMessage = 'Requête incorrecte';
              break;
            case 401:
              errorMessage = 'Non autorisé';
              break;
            case 403:
              errorMessage = 'Accès interdit';
              break;
            case 404:
              errorMessage = 'Ressource non trouvée';
              break;
            case 409:
              errorMessage = 'Conflit de données';
              break;
            case 500:
              errorMessage = 'Erreur serveur';
              break;
            default:
              errorMessage = `Erreur ${error.status}: ${error.error.message || error.statusText}`;
          }
          
          // Si on a un message d'erreur spécifique du serveur
          if (error.error && error.error.message) {
            errorMessage = error.error.message;
          } else if (error.error && typeof error.error === 'string') {
            errorMessage = error.error;
          }
        }

        // Afficher le message d'erreur dans un snackbar
        this.snackBar.open(errorMessage, 'Fermer', {
          duration: 5000,
          panelClass: ['error-snackbar']
        });

        return throwError(() => error);
      })
    );
  }
}