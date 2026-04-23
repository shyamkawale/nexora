import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { LoaderComponent } from '../../shared/components/loader.component';

@Component({
  selector: 'app-user-profile',
  standalone: true,
  imports: [CommonModule, LoaderComponent],
  templateUrl: './user.component.html',
  styleUrls: ['./user.component.scss']
})
export class UserProfileComponent implements OnInit {
  ngOnInit(): void {}
}

import { Routes } from '@angular/router';

export const userRoutes: Routes = [
  { path: '', component: UserProfileComponent }
];
