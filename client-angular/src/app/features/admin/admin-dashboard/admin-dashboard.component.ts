import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { HttpService } from '../../../core/services/http.service';
import { AuthService } from '../../../core/services/auth.service';
import { Router } from '@angular/router';

interface UserManagement {
  publicId: string;
  username: string;
  email: string;
  isActive: boolean;
  role: string;
}

@Component({
  selector: 'app-admin-dashboard',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './admin-dashboard.component.html',
  styleUrls: ['./admin-dashboard.component.scss']
})
export class AdminDashboardComponent implements OnInit {
  users: UserManagement[] = [];
  loading = false;
  errorMessage = '';

  constructor(
    private httpService: HttpService,
    private authService: AuthService,
    private router: Router
  ) {}

  ngOnInit(): void {
    // Check if user is admin
    if (!this.authService.isAdmin()) {
      this.router.navigate(['/unauthorized']);
      return;
    }
    this.loadUsers();
  }

  loadUsers(): void {
    this.loading = true;
    this.errorMessage = '';
    this.httpService.get<any>('/api/v1/admin/users').subscribe({
      next: (response: any) => {
        // Filter out admin users - only show regular users
        this.users = response.filter((user: UserManagement) => user.role === 'USER');
        this.loading = false;
      },
      error: (error) => {
        console.error('❌ Error loading users:', error);
        this.errorMessage = 'Failed to load users. ' + (error?.error?.message || '');
        this.loading = false;
      }
    });
  }

  deactivateUser(userId: string): void {
    if (confirm('Are you sure you want to deactivate this user?')) {
      this.httpService.put(`/api/v1/admin/users/${userId}/deactivate`, {}).subscribe({
        next: () => {
          this.loadUsers();
        },
        error: (error) => {
          alert('Failed to deactivate user: ' + (error?.error?.message || ''));
        }
      });
    }
  }

  activateUser(userId: string): void {
    if (confirm('Are you sure you want to activate this user?')) {
      this.httpService.put(`/api/v1/admin/users/${userId}/activate`, {}).subscribe({
        next: () => {
          this.loadUsers();
        },
        error: (error) => {
          alert('Failed to activate user: ' + (error?.error?.message || ''));
        }
      });
    }
  }

  deleteUser(userId: string): void {
    if (confirm('Are you sure you want to delete this user? This action cannot be undone.')) {
      this.httpService.delete(`/api/v1/admin/users/${userId}`).subscribe({
        next: () => {
          this.loadUsers();
        },
        error: (error) => {
          alert('Failed to delete user: ' + (error?.error?.message || ''));
        }
      });
    }
  }
}
