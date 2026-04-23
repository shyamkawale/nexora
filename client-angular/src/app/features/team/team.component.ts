import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { LoaderComponent } from '../../shared/components/loader.component';

@Component({
  selector: 'app-team',
  standalone: true,
  imports: [CommonModule, LoaderComponent],
  templateUrl: './team.component.html',
  styleUrls: ['./team.component.scss']
})
export class TeamComponent implements OnInit {
  ngOnInit(): void {}
}

import { Routes } from '@angular/router';

export const teamRoutes: Routes = [
  { path: '', component: TeamComponent }
];
