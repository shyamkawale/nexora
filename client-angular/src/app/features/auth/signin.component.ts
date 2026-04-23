import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule, ReactiveFormsModule, FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { MatIconModule } from '@angular/material/icon';
import { AuthService } from '../../core/services/auth.service';
import { ToastService } from '../../core/services/toast.service';
import { ErrorMessageComponent } from '../../shared/components/error-message.component';

@Component({
  selector: 'app-signin',
  standalone: true,
  imports: [CommonModule, FormsModule, ReactiveFormsModule, RouterLink, ErrorMessageComponent, MatIconModule],
  templateUrl: './signin.component.html',
  styleUrls: ['./signin.component.scss']
})
export class SigninComponent {
  signinForm: FormGroup;
  isLoading = false;
  errorMessage = '';
  showPassword = false;

  constructor(
    private fb: FormBuilder,
    private authService: AuthService,
    private toastService: ToastService,
    private router: Router
  ) {
    this.signinForm = this.fb.group({
      email: ['', [Validators.required, Validators.email]],
      password: ['', [Validators.required, Validators.minLength(2)]]
    });
  }

  togglePasswordVisibility(): void {
    this.showPassword = !this.showPassword;
  }

  onSubmit(): void {
    if (this.signinForm.invalid) return;

    this.isLoading = true;
    this.errorMessage = '';

    const { email, password } = this.signinForm.value;

    this.authService.signin(email, password).subscribe(
      response => {
        const username = response.user?.username || email;
        this.toastService.success(`Welcome back, ${username}! 👋`);
        setTimeout(() => {
          this.router.navigate(['/home']);
        }, 1000);
      },
      error => {
        this.errorMessage = error.message || 'Sign in failed. Please try again.';
        this.isLoading = false;
      }
    );
  }
}

