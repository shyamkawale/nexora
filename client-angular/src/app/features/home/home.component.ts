import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { StateService } from '../../core/services/state.service';
import { OrganizationService } from '../../core/services/organization.service';

@Component({
  selector: 'app-home',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './home.component.html',
  styleUrls: ['./home.component.scss']
})
export class HomeComponent implements OnInit {
  activeOrganization$ = this.organizationService.activeOrganization$;

  constructor(
    private stateService: StateService,
    private organizationService: OrganizationService
  ) {}

  ngOnInit(): void {
    // Initialize home component
  }
}
