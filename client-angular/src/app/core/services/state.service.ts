import { Injectable } from '@angular/core';
import { BehaviorSubject, Observable } from 'rxjs';
import { isPlatformBrowser } from '@angular/common';
import { PLATFORM_ID, Inject } from '@angular/core';

interface AppState {
  // Chat state
  chatId: string | null;
  recentChats: any[];
  activeRecentChat: string | null;
  activeChatTab: string | null;
  conversation: any[];
  currentChatPreview: any | null;

  // Content state
  posts: any[];
  singlePost: any | null;
  trendingPosts: any[];

  // User state
  profile: any | null;
  myProfile: any | null;
  people: any[];

  // UI state
  popup: any | null;
  confirmationPopup: any | null;
  navigation: any | null;
  leftNavigation: any | null;
  verticalLoader: boolean;
  fullScreenImage: any | null;

  // App state
  authenticated: boolean;
  serverDown: boolean;
}

const initialState: AppState = {
  chatId: null,
  recentChats: [],
  activeRecentChat: null,
  activeChatTab: null,
  conversation: [],
  currentChatPreview: null,
  posts: [],
  singlePost: null,
  trendingPosts: [],
  profile: null,
  myProfile: null,
  people: [],
  popup: null,
  confirmationPopup: null,
  navigation: null,
  leftNavigation: null,
  verticalLoader: false,
  fullScreenImage: null,
  authenticated: false,
  serverDown: false
};

@Injectable({
  providedIn: 'root'
})
export class StateService {
  private stateSubject = new BehaviorSubject<AppState>(initialState);
  public state$ = this.stateSubject.asObservable();
  private isBrowser: boolean;

  constructor(@Inject(PLATFORM_ID) platformId: Object) {
    this.isBrowser = isPlatformBrowser(platformId);
    if (this.isBrowser) {
      this.loadStateFromStorage();
    }
  }

  private loadStateFromStorage(): void {
    if (!this.isBrowser) return;
    
    const savedState = localStorage.getItem('appState');
    if (savedState) {
      try {
        const state = JSON.parse(savedState);
        this.stateSubject.next(state);
      } catch (e) {
        console.error('Error loading state from storage:', e);
      }
    }
  }

  private saveStateToStorage(): void {
    if (!this.isBrowser) return;
    
    localStorage.setItem('appState', JSON.stringify(this.stateSubject.value));
  }

  getState(): AppState {
    return this.stateSubject.value;
  }

  setState(partial: Partial<AppState>): void {
    const currentState = this.stateSubject.value;
    const newState = { ...currentState, ...partial };
    this.stateSubject.next(newState);
    this.saveStateToStorage();
  }

  resetState(): void {
    this.stateSubject.next(initialState);
    if (this.isBrowser) {
      localStorage.removeItem('appState');
    }
  }

  // Chat state methods
  setChatId(chatId: string): void {
    this.setState({ chatId });
  }

  setRecentChats(chats: any[]): void {
    this.setState({ recentChats: chats });
  }

  setConversation(conversation: any[]): void {
    this.setState({ conversation });
  }

  // User state methods
  setProfile(profile: any): void {
    this.setState({ profile });
  }

  setMyProfile(profile: any): void {
    this.setState({ myProfile: profile });
  }

  setPeople(people: any[]): void {
    this.setState({ people });
  }

  // UI state methods
  setPopup(popup: any): void {
    this.setState({ popup });
  }

  setConfirmationPopup(confirmationPopup: any): void {
    this.setState({ confirmationPopup });
  }

  setVerticalLoader(loading: boolean): void {
    this.setState({ verticalLoader: loading });
  }

  setFullScreenImage(image: any): void {
    this.setState({ fullScreenImage: image });
  }

  // App state methods
  setAuthenticated(authenticated: boolean): void {
    this.setState({ authenticated });
  }

  setServerDown(serverDown: boolean): void {
    this.setState({ serverDown });
  }

  // Posts state methods
  setPosts(posts: any[]): void {
    this.setState({ posts });
  }

  setSinglePost(post: any): void {
    this.setState({ singlePost: post });
  }

  setTrendingPosts(posts: any[]): void {
    this.setState({ trendingPosts: posts });
  }
}

