import { Component, OnInit, OnDestroy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule, Router } from '@angular/router';
import { AuthService } from './core/services/auth.service';
import { PresenceService } from './core/services/presence.service';
import { WebSocketService } from './core/services/websocket.service';
import { OrganizationService } from './core/services/organization.service';
import { ChatbotComponent } from './shared/components/chatbot/chatbot.component';
import { Subject, combineLatest, map } from 'rxjs';
import { takeUntil } from 'rxjs/operators';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [CommonModule, RouterModule, ChatbotComponent],
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.scss'],
  providers: [
  ]
})
export class AppComponent implements OnInit, OnDestroy {
  isAuthenticated = false;
  currentUser: any = null;
  private destroy$ = new Subject<void>();
  private presenceStarted = false;
  private activePresenceOrgId: string | null = null;

  isInitializing$ = this.authService.isInitializing$;
  isAuthenticated$ = this.authService.isAuthenticated$;
  currentUser$ = this.authService.currentUser$;
  organizations$ = this.organizationService.organizations$;
  activeOrganization$ = this.organizationService.activeOrganization$;
  approvedOrganizations$ = this.organizationService.organizations$.pipe(
    map(organizations => organizations.filter(org => org.status === 'APPROVED'))
  );
  isAdmin$ = this.authService.isAuthenticated$.pipe(
    map(() => this.authService.isAdmin())
  );

  constructor(
    private authService: AuthService,
    private presenceService: PresenceService,
    private webSocketService: WebSocketService,
    private organizationService: OrganizationService,
    private router: Router
  ) {}

  ngOnInit(): void {
    // Subscribe to authentication status for side effects only
    this.authService.isAuthenticated$
      .pipe(takeUntil(this.destroy$))
      .subscribe(isAuthenticated => {
        this.isAuthenticated = isAuthenticated;

        if (isAuthenticated) {
          this.organizationService.loadMyOrganizations()
            .pipe(takeUntil(this.destroy$))
            .subscribe({
              error: error => console.error('❌ Error loading organizations:', error)
            });
        } else {
          // Clean up on logout
          console.log('🚪 User logged out, cleaning up services...');
          this.presenceService.stopPresenceUpdates();
          this.webSocketService.disconnect();
          this.organizationService.clear();
          this.presenceStarted = false;
          this.activePresenceOrgId = null;
        }
      });

    combineLatest([this.authService.isAuthenticated$, this.organizationService.activeOrganization$])
      .pipe(takeUntil(this.destroy$))
      .subscribe(([isAuthenticated, organization]) => {
        const organizationId = organization?.publicId || null;
        if (isAuthenticated && organizationId) {
          if (!this.presenceStarted || this.activePresenceOrgId !== organizationId) {
            this.presenceService.stopPresenceUpdates();
            this.webSocketService.disconnect();
            console.log('🔐 Active organization selected, initializing realtime services...');
            this.webSocketService.connect();
            this.presenceService.startPresenceUpdates();
            this.presenceStarted = true;
            this.activePresenceOrgId = organizationId;
          }
          return;
        }

        if (this.presenceStarted) {
          this.presenceService.stopPresenceUpdates();
          this.webSocketService.disconnect();
          this.presenceStarted = false;
          this.activePresenceOrgId = null;
        }
      });
  }

  logout(): void {
    this.authService.logout()
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: () => {
          console.log('✅ Logged out successfully');
          this.router.navigate(['/auth/signin']);
        },
        error: (err) => {
          console.error('❌ Logout error:', err);
          // Even if API fails, clear auth locally and navigate
          this.authService.clearAuthLocally();
          this.router.navigate(['/auth/signin']);
        }
      });
  }

  navigateToProfile(): void {
    this.router.navigate(['/user']);
  }

  onOrganizationChange(event: Event): void {
    const organizationId = (event.target as HTMLSelectElement).value;
    this.organizationService.setActiveOrganizationById(organizationId);
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
    this.presenceService.stopPresenceUpdates();
    this.webSocketService.disconnect();
  }
}
