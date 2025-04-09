import { ComponentFixture, TestBed } from '@angular/core/testing';

import { PartnerDeleteDialogComponent } from './partner-delete-dialog.component';

describe('PartnerDeleteDialogComponent', () => {
  let component: PartnerDeleteDialogComponent;
  let fixture: ComponentFixture<PartnerDeleteDialogComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [PartnerDeleteDialogComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(PartnerDeleteDialogComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
