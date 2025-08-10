import {Component, signal} from '@angular/core';
import {AbstractControl, FormBuilder, FormGroup, ReactiveFormsModule, Validators} from '@angular/forms';
import {Router, RouterLink} from '@angular/router';
import {CommonModule} from '@angular/common';
import {MatCardModule} from '@angular/material/card';
import {MatFormFieldModule} from '@angular/material/form-field';
import {MatInputModule} from '@angular/material/input';
import {MatSelectModule} from '@angular/material/select';
import {MatButtonModule} from '@angular/material/button';
import {MatIconModule} from '@angular/material/icon';
import {MatProgressSpinnerModule} from '@angular/material/progress-spinner';
import {AuthService} from '../../services/auth.service';
import {RegisterRequest} from '../../interfaces/auth.interface';

@Component({
  selector: 'app-register',
  imports: [
    CommonModule,
    ReactiveFormsModule,
    RouterLink,
    MatCardModule,
    MatFormFieldModule,
    MatInputModule,
    MatSelectModule,
    MatButtonModule,
    MatIconModule,
    MatProgressSpinnerModule
  ],
  templateUrl: './register.html',
  styleUrl: './register.css'
})
export class Register {
  registerForm: FormGroup;
  isLoading = signal(false);
  errorMessage = signal<string | null>(null);
  emailExists = signal<boolean | null>(null);

  constructor(
    private fb: FormBuilder,
    private authService: AuthService,
    private router: Router
  ) {
    this.registerForm = this.fb.group({
      fullName: ['', [Validators.required, Validators.minLength(2), Validators.maxLength(255)]],
      email: ['', [Validators.required, Validators.email]],
      password: ['', [Validators.required, Validators.minLength(8)]],
      confirmPassword: ['', [Validators.required]],
      country: ['', [Validators.required]]
    }, {validators: this.passwordMatchValidator});

    this.registerForm.get('email')?.valueChanges.subscribe(email => {
      if (email && this.registerForm.get('email')?.valid) {
        this.checkEmailAvailability(email);
      } else {
        this.emailExists.set(null);
      }
    });
  }

  passwordMatchValidator(control: AbstractControl): { [key: string]: any } | null {
    const password = control.get('password');
    const confirmPassword = control.get('confirmPassword');

    if (password && confirmPassword && password.value !== confirmPassword.value) {
      return {'passwordMismatch': true};
    }
    return null;
  }

  onSubmit(): void {
    if (this.registerForm.valid && !this.isLoading() && !this.emailExists()) {
      this.isLoading.set(true);
      this.errorMessage.set(null);

      const {confirmPassword, ...registerData} = this.registerForm.value;
      const registerRequest: RegisterRequest = registerData;

      this.authService.register(registerRequest).subscribe({
        next: () => {
          this.isLoading.set(false);
          this.router.navigate(['/browse']);
        },
        error: (error) => {
          this.isLoading.set(false);
          this.errorMessage.set(error.error?.message || 'Registration failed. Please try again.');
        }
      });
    } else {
      this.markFormGroupTouched();
    }
  }

  private checkEmailAvailability(email: string): void {
    this.authService.checkEmailExists(email).subscribe({
      next: (exists) => {
        this.emailExists.set(exists);
      },
      error: () => {
        this.emailExists.set(null);
      }
    });
  }

  private markFormGroupTouched(): void {
    Object.keys(this.registerForm.controls).forEach(key => {
      const control = this.registerForm.get(key);
      control?.markAsTouched();
    });
  }

  getFieldError(fieldName: string): string | null {
    const field = this.registerForm.get(fieldName);
    if (field?.errors && field.touched) {
      if (field.errors['required']) {
        return `${this.getFieldDisplayName(fieldName)} is required`;
      }
      if (field.errors['email']) {
        return 'Please enter a valid email address';
      }
      if (field.errors['minlength']) {
        const requiredLength = field.errors['minlength'].requiredLength;
        return `${this.getFieldDisplayName(fieldName)} must be at least ${requiredLength} characters`;
      }
    }

    if (fieldName === 'confirmPassword' && this.registerForm.errors?.['passwordMismatch'] && field?.touched) {
      return 'Passwords do not match';
    }

    if (fieldName === 'email' && this.emailExists() === true && field?.valid) {
      return 'This email is already registered';
    }

    return null;
  }

  private getFieldDisplayName(fieldName: string): string {
    const displayNames: { [key: string]: string } = {
      fullName: 'Full Name',
      email: 'Email',
      password: 'Password',
      confirmPassword: 'Confirm Password',
      country: 'Country'
    };
    return displayNames[fieldName] || fieldName;
  }
}
