import { Component, Input } from '@angular/core';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-vertical-loader',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './vertical-loader.component.html',
  styleUrls: ['./vertical-loader.component.scss']
})
export class VerticalLoaderComponent {
  @Input() isLoading = true;
}
