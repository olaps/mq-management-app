import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

const routes: Routes = [
  {
    path: 'messages',
    loadChildren: () => import('./features/messages/messages.module').then(m => m.MessagesModule)
  },
  {
    path: 'partners',
    loadChildren: () => import('./features/partners/partners.module').then(m => m.PartnersModule)
  },
  { path: '', redirectTo: '/messages', pathMatch: 'full' },
  { path: '**', redirectTo: '/messages' }
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
