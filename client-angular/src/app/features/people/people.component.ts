import { Component, OnInit, OnDestroy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RouterModule, Router } from '@angular/router';
import { LoaderComponent } from '../../shared/components/loader.component';
import { PeopleService } from '../../core/services/people.service';
import { ChatService } from '../../core/services/chat.service';
import { PresenceService } from '../../core/services/presence.service';
import { OrganizationService } from '../../core/services/organization.service';
import { User } from '../../core/services/user.service';
import { Subject } from 'rxjs';
import { takeUntil, debounceTime, distinctUntilChanged, map } from 'rxjs/operators';
import { interval } from 'rxjs';

@Component({
  selector: 'app-people',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterModule, LoaderComponent],
  templateUrl: './people.component.html',
  styleUrls: ['./people.component.scss']
})
export class PeopleComponent implements OnInit, OnDestroy {
  isLoading = true;
  searchQuery = '';
  allUsers: User[] = [];
  filteredUsers: User[] = [];
  private destroy$ = new Subject<void>();
  private searchSubject = new Subject<string>();
  onlineUsers: string[] = [];

  constructor(
    private peopleService: PeopleService,
    private chatService: ChatService,
    private presenceService: PresenceService,
    private organizationService: OrganizationService,
    private router: Router
  ) {}

  ngOnInit(): void {
    console.log('👥 People component initialized');
    this.organizationService.activeOrganization$
      .pipe(
        map(organization => organization?.publicId || null),
        distinctUntilChanged(),
        takeUntil(this.destroy$)
      )
      .subscribe(organizationId => {
        this.resetPeople();
        if (organizationId) {
          this.loadUsers();
        } else {
          this.isLoading = false;
        }
      });
    this.setupSearch();
    this.subscribeToPresenceUpdates();
  }

  /**
   * Subscribe to real-time presence updates
   */
  private subscribeToPresenceUpdates(): void {
    // Listen for online users list updates
    this.presenceService.getOnlineUsers$()
      .pipe(takeUntil(this.destroy$))
      .subscribe(
        (onlineUsers: string[]) => {
          this.onlineUsers = onlineUsers;
          console.log('👥 Online users updated:', onlineUsers);
        }
      );

    // Listen for individual presence updates
    this.presenceService.getPresenceUpdates$()
      .pipe(takeUntil(this.destroy$))
      .subscribe(
        (update: any) => {
          console.log('📢 Presence update:', update);
          // Refresh online users list when any update is received
          this.presenceService.fetchOnlineUsersList();
        }
      );

    // Periodically refresh online users (every 10 seconds) as fallback
    interval(10000)
      .pipe(takeUntil(this.destroy$))
      .subscribe(() => {
        this.presenceService.fetchOnlineUsersList();
      });
  }

  /**
   * Check if user is online
   */
  isUserOnline(userId: string): boolean {
    return this.onlineUsers.includes(userId);
  }

  private loadUsers(): void {
    console.log('🔄 Loading users from API...');
    this.isLoading = true;
    this.peopleService.getAllUsers(0, 50)
      .pipe(takeUntil(this.destroy$))
      .subscribe(
        users => {
          console.log('✅ Raw response from API:', users);
          console.log('Users type:', typeof users);
          console.log('Is array?', Array.isArray(users));
          
          // Handle if response is wrapped in a data property
          let userList = users;
          if (users && typeof users === 'object' && !Array.isArray(users) && 'data' in users) {
            userList = users.data;
          }
          
          console.log('Processed users:', userList);
          this.allUsers = userList || [];
          this.filteredUsers = userList || [];
          console.log('✅ Users loaded successfully, count:', this.allUsers.length);
          
          if (this.allUsers.length > 0) {
            console.log('First user:', this.allUsers[0]);
          }
          
          // Fetch online users list immediately
          this.presenceService.fetchOnlineUsersList();
          
          this.isLoading = false;
        },
        error => {
          console.error('❌ Error loading users:', error);
          this.allUsers = [];
          this.filteredUsers = [];
          this.isLoading = false;
        }
      );
  }

  private resetPeople(): void {
    this.searchQuery = '';
    this.allUsers = [];
    this.filteredUsers = [];
    this.onlineUsers = [];
  }

  private setupSearch(): void {
    this.searchSubject.pipe(
      debounceTime(300),
      distinctUntilChanged(),
      takeUntil(this.destroy$)
    ).subscribe(query => {
      this.filterUsers(query);
    });
  }

  onSearch(query: string): void {
    this.searchSubject.next(query);
  }

  private filterUsers(query: string): void {
    if (!query.trim()) {
      this.filteredUsers = this.allUsers;
      return;
    }

    const lowerQuery = query.toLowerCase();
    this.filteredUsers = this.allUsers.filter(user => 
      user.username?.toLowerCase().includes(lowerQuery) ||
      user.email?.toLowerCase().includes(lowerQuery)
    );
  }

  openChat(user: User): void {
    if (!user.publicId) {
      console.error('User public ID not found');
      return;
    }

    this.chatService.getOrCreateDirectChat(user.publicId)
      .pipe(takeUntil(this.destroy$))
      .subscribe(
        chat => {
          // Navigate to chat with this user
          this.router.navigate(['/chat'], { state: { chatId: chat.publicId } });
        },
        error => {
          console.error('Error creating direct chat:', error);
        }
      );
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }
}

