import { Component, OnInit, OnDestroy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule, Router } from '@angular/router';
import { AuthService } from './core/services/auth.service';
import { PresenceService } from './core/services/presence.service';
import { WebSocketService } from './core/services/websocket.service';
import { ChatbotComponent } from './shared/components/chatbot/chatbot.component';
import { Subject, map } from 'rxjs';
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

  isInitializing$ = this.authService.isInitializing$;
  isAuthenticated$ = this.authService.isAuthenticated$;
  currentUser$ = this.authService.currentUser$;
  isAdmin$ = this.authService.isAuthenticated$.pipe(
    map(() => this.authService.isAdmin())
  );

  constructor(
    private authService: AuthService,
    private presenceService: PresenceService,
    private webSocketService: WebSocketService,
    private router: Router
  ) {}

  ngOnInit(): void {
    // Subscribe to authentication status for side effects only
    this.authService.isAuthenticated$
      .pipe(takeUntil(this.destroy$))
      .subscribe(isAuthenticated => {
        this.isAuthenticated = isAuthenticated;

        if (isAuthenticated && !this.presenceStarted) {
          // Connect WebSocket and start presence updates only once
          console.log('🔐 User authenticated, initializing services...');
          this.webSocketService.connect();
          this.presenceService.startPresenceUpdates();
          this.presenceStarted = true;
        } else if (!isAuthenticated) {
          // Clean up on logout
          console.log('🚪 User logged out, cleaning up services...');
          this.presenceService.stopPresenceUpdates();
          this.webSocketService.disconnect();
          this.presenceStarted = false;
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

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
    this.presenceService.stopPresenceUpdates();
    this.webSocketService.disconnect();
  }
}
