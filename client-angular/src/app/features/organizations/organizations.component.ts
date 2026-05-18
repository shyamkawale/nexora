import { CommonModule } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { FormsModule } from '@angular/forms';
import {
  Organization,
  OrganizationMember,
  OrganizationService
} from '../../core/services/organization.service';
import { AuthService } from '../../core/services/auth.service';

@Component({
  selector: 'app-organizations',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './organizations.component.html',
  styleUrls: ['./organizations.component.scss']
})
export class OrganizationsComponent implements OnInit {
  myOrganizations: Organization[] = [];
  publicOrganizations: Organization[] = [];
  members: OrganizationMember[] = [];
  pendingMembers: OrganizationMember[] = [];
  activeOrganization: Organization | null = null;

  createForm = {
    name: '',
    description: ''
  };

  loading = false;
  message = '';
  error = '';

  constructor(
    private organizationService: OrganizationService,
    private authService: AuthService
  ) {}

  ngOnInit(): void {
    this.organizationService.organizations$.subscribe(orgs => this.myOrganizations = orgs);
    this.organizationService.activeOrganization$.subscribe(org => {
      this.activeOrganization = org;
      this.loadMembers();
    });
    this.loadOrganizations();
  }

  loadOrganizations(): void {
    this.loading = true;
    this.error = '';

    this.organizationService.loadMyOrganizations().subscribe({
      next: () => {
        this.loading = false;
        this.loadPublicOrganizations();
      },
      error: error => {
        this.loading = false;
        this.error = error?.message || 'Failed to load your organizations';
      }
    });
  }

  loadPublicOrganizations(): void {
    this.organizationService.getOrganizations().subscribe({
      next: organizations => this.publicOrganizations = organizations || [],
      error: error => console.error('Error loading organizations', error)
    });
  }

  createOrganization(): void {
    if (!this.canCreateOrganizations()) {
      this.error = 'Only admin users can create organizations';
      return;
    }

    if (!this.createForm.name.trim()) {
      this.error = 'Organization name is required';
      return;
    }

    this.loading = true;
    this.error = '';
    this.message = '';

    this.organizationService.createOrganization({
      name: this.createForm.name.trim(),
      description: this.createForm.description.trim()
    }).subscribe({
      next: () => {
        this.loading = false;
        this.message = 'Organization created';
        this.createForm = { name: '', description: '' };
        this.loadPublicOrganizations();
      },
      error: error => {
        this.loading = false;
        this.error = error?.message || 'Failed to create organization';
      }
    });
  }

  requestJoin(organization: Organization): void {
    if (this.isAdminUser()) {
      this.setActiveOrganization(organization);
      return;
    }

    const membership = this.getMyMembership(organization);
    if (membership?.status === 'APPROVED') {
      this.setActiveOrganization(membership);
      return;
    }

    this.organizationService.requestJoin(organization.publicId).subscribe({
      next: () => {
        this.message = `Join request sent to ${organization.name}`;
        this.loadOrganizations();
      },
      error: error => this.error = error?.message || 'Failed to request access'
    });
  }

  setActiveOrganization(organization: Organization): void {
    this.organizationService.setActiveOrganization(organization);
  }

  approve(member: OrganizationMember): void {
    if (!this.activeOrganization || !member.user?.publicId) return;
    this.organizationService.approveMember(this.activeOrganization.publicId, member.user.publicId).subscribe({
      next: () => this.loadMembers(),
      error: error => this.error = error?.message || 'Failed to approve member'
    });
  }

  reject(member: OrganizationMember): void {
    if (!this.activeOrganization || !member.user?.publicId) return;
    this.organizationService.rejectMember(this.activeOrganization.publicId, member.user.publicId).subscribe({
      next: () => this.loadMembers(),
      error: error => this.error = error?.message || 'Failed to reject member'
    });
  }

  isJoined(organization: Organization): boolean {
    return this.myOrganizations.some(org => org.publicId === organization.publicId);
  }

  isApproved(organization: Organization): boolean {
    return this.getMyMembership(organization)?.status === 'APPROVED';
  }

  isPending(organization: Organization): boolean {
    return this.getMyMembership(organization)?.status === 'PENDING';
  }

  canManageMembers(): boolean {
    return this.isAdminUser() || this.activeOrganization?.role === 'ADMIN';
  }

  canCreateOrganizations(): boolean {
    return this.isAdminUser();
  }

  isAdminUser(): boolean {
    return this.authService.isAdmin();
  }

  private loadMembers(): void {
    if (!this.activeOrganization) {
      this.members = [];
      this.pendingMembers = [];
      return;
    }

    this.organizationService.getMembers(this.activeOrganization.publicId, this.canManageMembers()).subscribe({
      next: members => {
        this.members = (members || []).filter(member => member.status === 'APPROVED');
        this.pendingMembers = (members || []).filter(member => member.status === 'PENDING');
      },
      error: error => console.error('Error loading members', error)
    });
  }

  private getMyMembership(organization: Organization): Organization | undefined {
    return this.myOrganizations.find(org => org.publicId === organization.publicId);
  }
}
