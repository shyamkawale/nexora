import { Component, Input } from '@angular/core';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-full-screen-image-view',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './full-screen-image-view.component.html',
  styleUrls: ['./full-screen-image-view.component.scss']
})
export class FullScreenImageViewComponent {
  @Input() isVisible = false;
  @Input() imageUrl = '';
  @Input() imageAlt = 'Full screen image';

  close(): void {
    // Emit close event or handle closing
  }
}
