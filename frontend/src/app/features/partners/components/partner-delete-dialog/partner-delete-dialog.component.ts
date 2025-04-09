// partner-delete-dialog.component.ts
import { Component, Inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MAT_DIALOG_DATA, MatDialogRef, MatDialogModule } from '@angular/material/dialog';
import { MatButtonModule } from '@angular/material/button';

import { Partner } from '../../../../core/models/partner.model';

@Component({
  selector: 'app-partner-delete-dialog',
  standalone: true,
  imports: [
    CommonModule,
    MatDialogModule,
    MatButtonModule
  ],
  templateUrl: './partner-delete-dialog.component.html',
  styleUrls: ['./partner-delete-dialog.component.scss']
})
export class PartnerDeleteDialogComponent {
  constructor(
    public dialogRef: MatDialogRef<PartnerDeleteDialogComponent>,
    @Inject(MAT_DIALOG_DATA) public partner: Partner
  ) {}
  
  cancel(): void {
    this.dialogRef.close(false);
  }
  
  confirm(): void {
    this.dialogRef.close(true);
  }
}