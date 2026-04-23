import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { LoaderComponent } from '../../shared/components/loader.component';

@Component({
  selector: 'app-org-rules',
  standalone: true,
  imports: [CommonModule, LoaderComponent],
  templateUrl: './org-rules.component.html',
  styleUrls: ['./org-rules.component.scss']
})
export class OrgRulesComponent implements OnInit {
  ngOnInit(): void {}
}

import { Routes } from '@angular/router';

export const orgRulesRoutes: Routes = [
  { path: '', component: OrgRulesComponent }
];
