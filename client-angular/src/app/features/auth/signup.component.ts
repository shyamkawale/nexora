import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule, ReactiveFormsModule, FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { MatIconModule } from '@angular/material/icon';
import { AuthService } from '../../core/services/auth.service';
import { ToastService } from '../../core/services/toast.service';
import { ErrorMessageComponent } from '../../shared/components/error-message.component';

@Component({
  selector: 'app-signup',
  standalone: true,
  imports: [CommonModule, FormsModule, ReactiveFormsModule, RouterLink, ErrorMessageComponent, MatIconModule],
  templateUrl: './signup.component.html',
  styleUrls: ['./signup.component.scss']
})
export class SignupComponent {
  signupForm: FormGroup;
  isLoading = false;
  errorMessage = '';
  passwordMismatchError = '';
  showPassword = false;
  showConfirmPassword = false;

  constructor(
    private fb: FormBuilder,
    private authService: AuthService,
    private toastService: ToastService,
    private router: Router
  ) {
    this.signupForm = this.fb.group({
      username: ['', [Validators.required, Validators.minLength(2)]],
      email: ['', [Validators.required, Validators.email]],
      password: ['', [Validators.required, Validators.minLength(2)]],
      confirmPassword: ['', Validators.required]
    });
  }

  togglePasswordVisibility(field: 'password' | 'confirm'): void {
    if (field === 'password') {
      this.showPassword = !this.showPassword;
    } else {
      this.showConfirmPassword = !this.showConfirmPassword;
    }
  }

  onSubmit(): void {
    if (this.signupForm.invalid) return;

    const { password, confirmPassword } = this.signupForm.value;

    if (password !== confirmPassword) {
      this.passwordMismatchError = 'Passwords do not match';
      return;
    }

    this.passwordMismatchError = '';
    this.isLoading = true;
    this.errorMessage = '';

    this.authService.signup(this.signupForm.value).subscribe(
      response => {
        const username = this.signupForm.value.username;
        this.toastService.success(`Account created successfully! Redirecting to login...`);
        setTimeout(() => {
          this.router.navigate(['/auth/signin']);
        }, 1500);
      },
      error => {
        this.errorMessage = error.message || 'Sign up failed. Please try again.';
        this.isLoading = false;
      }
    );
  }
}
