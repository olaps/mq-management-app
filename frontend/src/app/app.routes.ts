import { Routes } from '@angular/router';
import { MessageListComponent } from './features/messages/components/message-list/message-list.component';
import { PartnerListComponent } from './features/partners/components/partner-list/partner-list.component';
import { PartnerFormComponent } from './features/partners/components/partner-form/partner-form.component';

export const routes: Routes = [
  { path: '', redirectTo: 'messages', pathMatch: 'full' },
  { path: 'messages', component: MessageListComponent },
  { path: 'partners', component: PartnerListComponent },
  { path: 'partners/new', component: PartnerFormComponent },
  { path: 'partners/edit/:id', component: PartnerFormComponent },
  { path: '**', redirectTo: 'messages' } // Route pour gérer les URL inconnues
];