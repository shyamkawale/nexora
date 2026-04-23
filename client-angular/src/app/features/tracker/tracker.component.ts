import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { LoaderComponent } from '../../shared/components/loader.component';

@Component({
  selector: 'app-tracker',
  standalone: true,
  imports: [CommonModule, LoaderComponent],
  templateUrl: './tracker.component.html',
  styleUrls: ['./tracker.component.scss']
})
export class TrackerComponent implements OnInit {
  ngOnInit(): void {}
}

import { Routes } from '@angular/router';

export const trackerRoutes: Routes = [
  { path: '', component: TrackerComponent }
];
