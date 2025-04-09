// partner-form.component.ts
import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule, FormBuilder, FormGroup, Validators } from '@angular/forms';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';

// Angular Material imports
import { MatCardModule } from '@angular/material/card';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { MatButtonModule } from '@angular/material/button';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';

// Services, models and components
import { PartnerService } from '../../../../core/services/partner.service';
import { Partner, Direction, ProcessedFlowType } from '../../../../core/models/partner.model';
import { LoadingSpinnerComponent } from '../../../../shared/components/loading-spinner/loading-spinner.component';

@Component({
  selector: 'app-partner-form',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    RouterLink,
    MatCardModule,
    MatFormFieldModule,
    MatInputModule,
    MatSelectModule,
    MatButtonModule,
    MatSnackBarModule,
    LoadingSpinnerComponent
  ],
  templateUrl: './partner-form.component.html',
  styleUrls: ['./partner-form.component.scss']
})
export class PartnerFormComponent implements OnInit {
  partnerForm!: FormGroup;
  isEditMode = false;
  partnerId?: number;
  loading = false;
  
  directions = Object.values(Direction);
  flowTypes = Object.values(ProcessedFlowType);
  
  constructor(
    private fb: FormBuilder,
    private partnerService: PartnerService,
    private route: ActivatedRoute,
    private router: Router,
    private snackBar: MatSnackBar
  ) {}
  
  ngOnInit(): void {
    this.initForm();
    
    this.route.params.subscribe(params => {
      if (params['id']) {
        this.isEditMode = true;
        this.partnerId = +params['id'];
        this.loadPartner(this.partnerId);
      }
    });
  }
  
  initForm(): void {
    this.partnerForm = this.fb.group({
      alias: ['', [Validators.required]],
      type: ['', [Validators.required]],
      direction: [Direction.INBOUND, [Validators.required]],
      application: [''],
      processedFlowType: [ProcessedFlowType.MESSAGE, [Validators.required]],
      description: ['', [Validators.required]]
    });
  }
  
  loadPartner(id: number): void {
    this.loading = true;
    
    this.partnerService.getPartner(id).subscribe({
      next: (partner) => {
        this.partnerForm.patchValue(partner);
        this.loading = false;
      },
      error: (error) => {
        this.snackBar.open('Erreur lors du chargement du partenaire', 'Fermer', {
          duration: 3000
        });
        this.loading = false;
        this.router.navigate(['/partners']);
      }
    });
  }
  
  savePartner(): void {
    if (this.partnerForm.invalid) {
      this.markFormGroupTouched(this.partnerForm);
      return;
    }
    
    this.loading = true;
    const partner: Partner = this.partnerForm.value;
    
    if (this.isEditMode && this.partnerId) {
      this.partnerService.updatePartner(this.partnerId, partner).subscribe({
        next: () => {
          this.snackBar.open('Partenaire mis à jour avec succès', 'Fermer', {
            duration: 3000
          });
          this.router.navigate(['/partners']);
        },
        error: (error) => {
          this.snackBar.open('Erreur lors de la mise à jour du partenaire', 'Fermer', {
            duration: 3000
          });
          this.loading = false;
        }
      });
    } else {
      this.partnerService.createPartner(partner).subscribe({
        next: () => {
          this.snackBar.open('Partenaire créé avec succès', 'Fermer', {
            duration: 3000
          });
          this.router.navigate(['/partners']);
        },
        error: (error) => {
          this.snackBar.open('Erreur lors de la création du partenaire', 'Fermer', {
            duration: 3000
          });
          this.loading = false;
        }
      });
    }
  }
  
  cancel(): void {
    this.router.navigate(['/partners']);
  }
  
  // Marquer tous les champs comme touchés pour afficher les erreurs de validation
  markFormGroupTouched(formGroup: FormGroup): void {
    Object.values(formGroup.controls).forEach(control => {
      control.markAsTouched();
      if (control instanceof FormGroup) {
        this.markFormGroupTouched(control);
      }
    });
  }
}