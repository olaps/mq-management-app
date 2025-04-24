
import { Routes } from '@angular/router';
import { LoginComponent } from './features/auth/login/login.component';
import { RegisterComponent } from './features/auth/register/register.component';
import { MessageListComponent } from './features/messages/components/message-list/message-list.component';
import { PartnerListComponent } from './features/partners/components/partner-list/partner-list.component';
import { PartnerFormComponent } from './features/partners/components/partner-form/partner-form.component';
import { AuthGuard } from './core/guards/auth.guard';

export const routes: Routes = [
  { path: '', redirectTo: '/messages', pathMatch: 'full' },
  { path: 'login', component: LoginComponent },
  { path: 'register', component: RegisterComponent },
  { 
    path: 'messages', 
    component: MessageListComponent,
    canActivate: [AuthGuard],
    data: { roles: ['ROLE_USER', 'ROLE_SUPERVISOR', 'ROLE_ADMIN'] }
  },
  { 
    path: 'partners', 
    component: PartnerListComponent,
    canActivate: [AuthGuard],
    data: { roles: ['ROLE_SUPERVISOR', 'ROLE_ADMIN'] }
  },
  { 
    path: 'partners/new', 
    component: PartnerFormComponent,
    canActivate: [AuthGuard],
    data: { roles: ['ROLE_SUPERVISOR', 'ROLE_ADMIN'] }
  },
  { 
    path: 'partners/edit/:id', 
    component: PartnerFormComponent,
    canActivate: [AuthGuard],
    data: { roles: ['ROLE_SUPERVISOR', 'ROLE_ADMIN'] }
  },
  // Route par défaut
  { path: '**', redirectTo: '/messages' }
];