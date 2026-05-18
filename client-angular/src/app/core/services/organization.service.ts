import { Injectable, Inject, PLATFORM_ID } from '@angular/core';
import { BehaviorSubject, Observable, tap } from 'rxjs';
import { isPlatformBrowser } from '@angular/common';
import { HttpService } from './http.service';

export type OrganizationRole = 'ADMIN' | 'MEMBER';
export type OrganizationMemberStatus = 'PENDING' | 'APPROVED' | 'REJECTED';

export interface Organization {
  publicId: string;
  name: string;
  description?: string;
  isActive: boolean;
  role?: OrganizationRole;
  status?: OrganizationMemberStatus;
  createdAt?: string;
  updatedAt?: string;
}

export interface OrganizationMember {
  organizationPublicId: string;
  user: any;
  role: OrganizationRole;
  status: OrganizationMemberStatus;
  joinedAt?: string;
  createdAt?: string;
}

@Injectable({
  providedIn: 'root'
})
export class OrganizationService {
  private readonly activeOrgStorageKey = 'activeOrganizationId';
  private isBrowser: boolean;

  private organizationsSubject = new BehaviorSubject<Organization[]>([]);
  organizations$ = this.organizationsSubject.asObservable();

  private activeOrganizationSubject = new BehaviorSubject<Organization | null>(null);
  activeOrganization$ = this.activeOrganizationSubject.asObservable();

  constructor(
    private httpService: HttpService,
    @Inject(PLATFORM_ID) platformId: Object
  ) {
    this.isBrowser = isPlatformBrowser(platformId);
  }

  loadMyOrganizations(): Observable<Organization[]> {
    return this.httpService.get<Organization[]>('/api/v1/organizations/me').pipe(
      tap(organizations => this.setOrganizations(organizations || []))
    );
  }

  getOrganizations(): Observable<Organization[]> {
    return this.httpService.get<Organization[]>('/api/v1/organizations');
  }

  createOrganization(request: { name: string; description?: string }): Observable<Organization> {
    return this.httpService.post<Organization>('/api/v1/organizations', request).pipe(
      tap(org => {
        this.setOrganizations([org, ...this.organizationsSubject.value.filter(item => item.publicId !== org.publicId)]);
        this.setActiveOrganization(org);
      })
    );
  }

  requestJoin(organizationId: string): Observable<OrganizationMember> {
    return this.httpService.post<OrganizationMember>(`/api/v1/organizations/${organizationId}/join`, {});
  }

  approveMember(organizationId: string, userId: string): Observable<OrganizationMember> {
    return this.httpService.put<OrganizationMember>(`/api/v1/organizations/${organizationId}/members/${userId}/approve`, {});
  }

  rejectMember(organizationId: string, userId: string): Observable<OrganizationMember> {
    return this.httpService.put<OrganizationMember>(`/api/v1/organizations/${organizationId}/members/${userId}/reject`, {});
  }

  getMembers(organizationId: string, includePending = false): Observable<OrganizationMember[]> {
    return this.httpService.get<OrganizationMember[]>(`/api/v1/organizations/${organizationId}/members?includePending=${includePending}`);
  }

  setActiveOrganizationById(publicId: string): void {
    const organization = this.organizationsSubject.value.find(org => org.publicId === publicId) || null;
    this.setActiveOrganization(organization);
  }

  setActiveOrganization(organization: Organization | null): void {
    if (organization?.status && organization.status !== 'APPROVED') {
      return;
    }

    this.activeOrganizationSubject.next(organization);
    if (!this.isBrowser) return;

    if (organization) {
      localStorage.setItem(this.activeOrgStorageKey, organization.publicId);
    } else {
      localStorage.removeItem(this.activeOrgStorageKey);
    }
  }

  getActiveOrganizationIdSnapshot(): string | null {
    return this.activeOrganizationSubject.value?.publicId || this.getStoredOrganizationId();
  }

  clear(): void {
    this.organizationsSubject.next([]);
    this.setActiveOrganization(null);
  }

  private setOrganizations(organizations: Organization[]): void {
    this.organizationsSubject.next(organizations);

    const approved = organizations.filter(org => org.status === 'APPROVED');
    const storedId = this.getStoredOrganizationId();
    const storedOrg = approved.find(org => org.publicId === storedId);
    const currentOrg = this.activeOrganizationSubject.value;
    const stillAvailable = currentOrg
      ? approved.find(org => org.publicId === currentOrg.publicId)
      : null;

    this.setActiveOrganization(stillAvailable || storedOrg || approved[0] || null);
  }

  private getStoredOrganizationId(): string | null {
    if (!this.isBrowser) return null;
    return localStorage.getItem(this.activeOrgStorageKey);
  }
}
