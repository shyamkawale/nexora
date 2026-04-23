import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { LoaderComponent } from '../../shared/components/loader.component';

@Component({
  selector: 'app-feeds',
  standalone: true,
  imports: [CommonModule, RouterModule, LoaderComponent],
  templateUrl: './feeds.component.html',
  styleUrls: ['./feeds.component.scss']
})
export class FeedsComponent implements OnInit {
  ngOnInit(): void {
    // Initialize feeds component
  }
}
