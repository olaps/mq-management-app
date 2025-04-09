
import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { PartnerListComponent } from './components/partner-list/partner-list.component';
import { PartnerFormComponent } from './components/partner-form/partner-form.component';

const routes: Routes = [
  { path: '', component: PartnerListComponent },
  { path: 'new', component: PartnerFormComponent },
  { path: 'edit/:id', component: PartnerFormComponent }
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class PartnersRoutingModule { }
