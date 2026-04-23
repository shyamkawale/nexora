import { Injectable, OnDestroy } from '@angular/core';
import { interval, Subject, BehaviorSubject } from 'rxjs';
import { takeUntil } from 'rxjs/operators';
import { WebSocketService } from './websocket.service';
import { HttpService } from './http.service';

@Injectable({
  providedIn: 'root'
})
export class PresenceService implements OnDestroy {
  private destroy$ = new Subject<void>();
  private presenceInterval: any;
  private isPresenceActive = false;

  // Observable for presence updates
  presenceUpdates$ = new Subject<any>();
  onlineUsers$ = new BehaviorSubject<string[]>([]);

  constructor(
    private webSocketService: WebSocketService,
    private httpService: HttpService
  ) {
    this.subscribeToPresenceUpdates();
    // Fetch initial online users list
    // this.fetchOnlineUsersList();
    // Setup handler for browser close without logout
    this.setupUnloadHandler();
  }

  /**
   * Send offline status when user closes browser/tab without logging out
   */
  private setupUnloadHandler(): void {
    if (typeof window === 'undefined') return;
    
    window.addEventListener('beforeunload', () => {
      console.log('📴 User closed browser - marking as offline via sendBeacon');
      // Use sendBeacon for guaranteed delivery even on page unload
      navigator.sendBeacon('/api/v1/user/presence-offline', '');
    });
  }

  startPresenceUpdates(): void {
    // Prevent multiple intervals
    if (this.isPresenceActive) {
      console.log('⏸️  Presence updates already active, skipping duplicate start');
      return;
    }

    this.isPresenceActive = true;
    console.log('🟢 Starting presence updates (every 15 seconds)');

    // Update presence every 15 seconds
    this.presenceInterval = interval(15000)
      .pipe(takeUntil(this.destroy$))
      .subscribe(() => {
        this.updatePresence();
      });

    // Initial update
    this.updatePresence();
  }

  private updatePresence(): void {
    console.log('📍 Updating user presence...');
    this.httpService.post('/api/v1/user/presence', {
      timestamp: new Date().toISOString(),
      status: 'online'
    }).pipe(takeUntil(this.destroy$))
      .subscribe(
        (response: any) => {
          console.log('✅ Presence updated successfully');
        },
        error => console.error('❌ Error updating presence:', error)
      );
  }

  /**
   * Subscribe to real-time presence updates via WebSocket
   */
  private subscribeToPresenceUpdates(): void {
    this.webSocketService.subscribeToChannel('/topic/presence/updates')
      .pipe(takeUntil(this.destroy$))
      .subscribe(
        (message: any) => {
          console.log('📢 Presence update received:', message);
          this.presenceUpdates$.next(message);
          this.updateOnlineUsersList();
        },
        error => console.error('❌ Error subscribing to presence updates:', error)
      );
  }

  /**
   * Refresh online users list from server
   */
  fetchOnlineUsersList(): void {
    this.httpService.get('/api/v1/presence/online-users')
      .pipe(takeUntil(this.destroy$))
      .subscribe(
        (response: any) => {
          console.log('👥 Online users fetched:', response.onlineUsers);
          this.onlineUsers$.next(response.onlineUsers || []);
        },
        error => console.error('❌ Error fetching online users:', error)
      );
  }

  /**
   * Refresh online users list from server (alias)
   */
  private updateOnlineUsersList(): void {
    this.fetchOnlineUsersList();
  }

  /**
   * Check if a specific user is online
   */
  isUserOnline(userId: string): boolean {
    const onlineUsers = this.onlineUsers$.getValue();
    return onlineUsers.includes(userId);
  }

  /**
   * Get observable of online users
   */
  getOnlineUsers$() {
    return this.onlineUsers$.asObservable();
  }

  /**
   * Get observable of presence updates
   */
  getPresenceUpdates$() {
    return this.presenceUpdates$.asObservable();
  }

  stopPresenceUpdates(): void {
    console.log('🔴 Stopping presence updates');
    if (this.presenceInterval) {
      clearInterval(this.presenceInterval);
      this.presenceInterval = null;
    }
    this.isPresenceActive = false;
    this.destroy$.next();
  }

  ngOnDestroy(): void {
    this.stopPresenceUpdates();
  }
}

