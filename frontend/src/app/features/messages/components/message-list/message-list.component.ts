// message-list.component.ts
import { Component, OnInit, ViewChild } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule, FormBuilder, FormGroup } from '@angular/forms';
import { MatPaginator, MatPaginatorModule } from '@angular/material/paginator';
import { MatSort, MatSortModule } from '@angular/material/sort';
import { MatTableDataSource, MatTableModule } from '@angular/material/table';
import { MatDialog, MatDialogModule } from '@angular/material/dialog';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatChipsModule } from '@angular/material/chips';
import { MatTooltipModule } from '@angular/material/tooltip';
import { MatCardModule } from '@angular/material/card';
import { debounceTime, distinctUntilChanged } from 'rxjs/operators';

import { MessageService } from '../../../../core/services/message.service';
import { Message } from '../../../../core/models/message.model';
import { MessageDetailComponent } from '../message-detail/message-detail.component';
import { LoadingSpinnerComponent } from '../../../../shared/components/loading-spinner/loading-spinner.component';

@Component({
  selector: 'app-message-list',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    MatTableModule,
    MatPaginatorModule,
    MatSortModule,
    MatCardModule,
    MatFormFieldModule,
    MatInputModule,
    MatSelectModule,
    MatButtonModule,
    MatIconModule,
    MatDialogModule,
    MatChipsModule,
    MatTooltipModule,
    LoadingSpinnerComponent
  ],
  templateUrl: './message-list.component.html',
  styleUrls: ['./message-list.component.scss']
})
export class MessageListComponent implements OnInit {
  displayedColumns: string[] = ['id', 'messageId', 'queueName', 'messageType', 'receivedAt', 'processed', 'actions'];
  dataSource = new MatTableDataSource<Message>([]);
  totalElements = 0;
  pageSize = 20;
  currentPage = 0;
  
  loading = false;
  filterForm: FormGroup;
  
  @ViewChild(MatPaginator) paginator!: MatPaginator;
  @ViewChild(MatSort) sort!: MatSort;
  
  constructor(
    private messageService: MessageService,
    private dialog: MatDialog,
    private fb: FormBuilder
  ) {
    this.filterForm = this.fb.group({
      keyword: [''],
      processed: [''],
      queueName: ['']
    });
  }
  
  ngOnInit(): void {
    this.loadMessages();
    
    this.filterForm.valueChanges
      .pipe(
        debounceTime(500),
        distinctUntilChanged()
      )
      .subscribe(() => {
        this.paginator.firstPage();
        this.loadMessages();
      });
  }
  
  loadMessages(): void {
    this.loading = true;
    
    const filters = this.filterForm.value;
    
    if (filters.keyword) {
      this.messageService.searchMessages(filters.keyword, this.currentPage, this.pageSize)
        .subscribe(this.handleMessagesResponse.bind(this));
    } else if (filters.processed !== '') {
      this.messageService.getMessagesByProcessed(filters.processed === 'true', this.currentPage, this.pageSize)
        .subscribe(this.handleMessagesResponse.bind(this));
    } else if (filters.queueName) {
      this.messageService.getMessagesByQueueName(filters.queueName, this.currentPage, this.pageSize)
        .subscribe(this.handleMessagesResponse.bind(this));
    } else {
      this.messageService.getMessages(this.currentPage, this.pageSize)
        .subscribe(this.handleMessagesResponse.bind(this));
    }
  }
  
  handleMessagesResponse(response: any): void {
    this.dataSource.data = response.content;
    this.totalElements = response.totalElements;
    this.loading = false;
  }
  
  pageChanged(event: any): void {
    this.pageSize = event.pageSize;
    this.currentPage = event.pageIndex;
    this.loadMessages();
  }
  
  resetFilters(): void {
    this.filterForm.reset({
      keyword: '',
      processed: '',
      queueName: ''
    });
    this.paginator.firstPage();
    this.loadMessages();
  }
  
  openMessageDetail(message: Message): void {
    this.dialog.open(MessageDetailComponent, {
      width: '800px',
      data: message
    });
  }
  
  getProcessedStatusClass(processed: boolean): string {
    return processed ? 'processed' : 'not-processed';
  }
  
  formatDate(date: string): string {
    return new Date(date).toLocaleString();
  }
}