// partner-list.component.ts
import { Component, OnInit, ViewChild } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule, FormBuilder, FormGroup } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';

// Angular Material imports
import { MatTableDataSource, MatTableModule } from '@angular/material/table';
import { MatPaginatorModule, MatPaginator } from '@angular/material/paginator';
import { MatSortModule, MatSort } from '@angular/material/sort';
import { MatCardModule } from '@angular/material/card';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatTooltipModule } from '@angular/material/tooltip';
import { MatDialogModule, MatDialog } from '@angular/material/dialog';
import { MatChipsModule } from '@angular/material/chips';

// Services and components
import { PartnerService } from '../../../../core/services/partner.service';
import { Partner } from '../../../../core/models/partner.model';
import { PartnerDeleteDialogComponent } from '../partner-delete-dialog/partner-delete-dialog.component';
import { LoadingSpinnerComponent } from '../../../../shared/components/loading-spinner/loading-spinner.component';
import { debounceTime, distinctUntilChanged } from 'rxjs';

@Component({
  selector: 'app-partner-list',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    MatTableModule,
    MatPaginatorModule,
    MatSortModule,
    MatCardModule,
    MatButtonModule,
    MatIconModule,
    MatFormFieldModule,
    MatInputModule,
    MatTooltipModule,
    MatDialogModule,
    MatChipsModule,
    LoadingSpinnerComponent
  ],
  templateUrl: './partner-list.component.html',
  styleUrls: ['./partner-list.component.scss']
})
export class PartnerListComponent implements OnInit {
  displayedColumns: string[] = ['id', 'alias', 'type', 'direction', 'application', 'processedFlowType', 'description', 'actions'];
  dataSource = new MatTableDataSource<Partner>([]);
  totalElements = 0;
  pageSize = 20;
  currentPage = 0;
  
  loading = false;
  searchForm: FormGroup;
  
  @ViewChild(MatPaginator) paginator!: MatPaginator;
  @ViewChild(MatSort) sort!: MatSort;
  
  constructor(
    private partnerService: PartnerService,
    private dialog: MatDialog,
    private fb: FormBuilder,
    private router: Router
  ) {
    this.searchForm = this.fb.group({
      keyword: ['']
    });
  }
  
  ngOnInit(): void {
    this.loadPartners();
    
    this.searchForm.valueChanges
      .pipe(
        debounceTime(500),
        distinctUntilChanged()
      )
      .subscribe(() => {
        this.paginator.firstPage();
        this.loadPartners();
      });
  }
  
  loadPartners(): void {
    this.loading = true;
    
    const keyword = this.searchForm.get('keyword')?.value;
    
    if (keyword) {
      this.partnerService.searchPartners(keyword, this.currentPage, this.pageSize)
        .subscribe(this.handlePartnersResponse.bind(this));
    } else {
      this.partnerService.getPartners(this.currentPage, this.pageSize)
        .subscribe(this.handlePartnersResponse.bind(this));
    }
  }
  
  handlePartnersResponse(response: any): void {
    this.dataSource.data = response.content;
    this.totalElements = response.totalElements;
    this.loading = false;
  }
  
  pageChanged(event: any): void {
    this.pageSize = event.pageSize;
    this.currentPage = event.pageIndex;
    this.loadPartners();
  }
  
  resetSearch(): void {
    this.searchForm.reset({ keyword: '' });
    this.paginator.firstPage();
    this.loadPartners();
  }
  
  editPartner(partner: Partner): void {
    this.router.navigate(['/partners/edit', partner.id]);
  }
  
  addPartner(): void {
    this.router.navigate(['/partners/new']);
  }
  
  deletePartner(partner: Partner): void {
    const dialogRef = this.dialog.open(PartnerDeleteDialogComponent, {
      width: '400px',
      data: partner
    });
    
    dialogRef.afterClosed().subscribe(result => {
      if (result) {
        this.partnerService.deletePartner(partner.id!).subscribe(() => {
          this.loadPartners();
        });
      }
    });
  }
  
  getDirectionClass(direction: string): string {
    return direction === 'INBOUND' ? 'inbound' : 'outbound';
  }
  
  getFlowTypeClass(flowType: string): string {
    switch (flowType) {
      case 'MESSAGE': return 'message';
      case 'ALERTING': return 'alerting';
      case 'NOTIFICATION': return 'notification';
      default: return '';
    }
  }
  
  formatDate(date: string): string {
    return new Date(date).toLocaleString();
  }
}
