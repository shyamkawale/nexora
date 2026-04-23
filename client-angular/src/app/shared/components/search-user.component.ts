import { Component, Input, Output, EventEmitter } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-search-user',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './search-user.component.html',
  styleUrls: ['./search-user.component.scss']
})
export class SearchUserComponent {
  @Input() users: any[] = [];
  @Output() userSelected = new EventEmitter<any>();

  searchQuery = '';
  results: any[] = [];
  showResults = false;

  onSearch(): void {
    if (!this.searchQuery.trim()) {
      this.results = [];
      this.showResults = false;
      return;
    }

    const query = this.searchQuery.toLowerCase();
    this.results = this.users.filter(user =>
      user.name.toLowerCase().includes(query) ||
      user.email.toLowerCase().includes(query)
    );
    this.showResults = true;
  }

  selectUser(user: any): void {
    this.userSelected.emit(user);
    this.searchQuery = '';
    this.results = [];
    this.showResults = false;
  }
}
