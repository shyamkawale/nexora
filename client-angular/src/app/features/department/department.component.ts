import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { LoaderComponent } from '../../shared/components/loader.component';

@Component({
  selector: 'app-department',
  standalone: true,
  imports: [CommonModule, LoaderComponent],
  templateUrl: './department.component.html',
  styleUrls: ['./department.component.scss']
})
export class DepartmentComponent implements OnInit {
  ngOnInit(): void {}
}

import { Routes } from '@angular/router';

export const departmentRoutes: Routes = [
  { path: '', component: DepartmentComponent }
];
