import { Injectable, Inject, PLATFORM_ID } from '@angular/core';
import { Subject, Observable, BehaviorSubject } from 'rxjs';
import { environment } from '../../../environments/environment';
import { Client } from '@stomp/stompjs';
import SockJS from 'sockjs-client';
import { isPlatformBrowser } from '@angular/common';

@Injectable({
  providedIn: 'root'
})
export class WebSocketService {
  private client: Client | null = null;
  private messageSubject = new Subject<any>();
  private connectionStatusSubject = new BehaviorSubject<boolean>(false);
  private reconnectAttempts = 0;
  private maxReconnectAttempts = 5;
  private reconnectDelay = 3000;
  private isBrowser: boolean;
  private activeSubscriptions = new Map<string, any>();

  public message$ = this.messageSubject.asObservable();
  public connectionStatus$ = this.connectionStatusSubject.asObservable();

  constructor(@Inject(PLATFORM_ID) platformId: Object) {
    this.isBrowser = isPlatformBrowser(platformId);
  }

  connect(): void {
    // Skip WebSocket connection during SSR
    if (!this.isBrowser) {
      console.log('⏸️ Skipping WebSocket connection (SSR context)');
      return;
    }

    if (this.client && this.client.connected) {
      console.log('✅ STOMP already connected');
      return;
    }

    const wsUrl = environment.websocketUrl;
    console.log('📡 Attempting STOMP connection to:', wsUrl);
    
    this.client = new Client({
      // brokerURL: wsUrl,
      webSocketFactory: () => new SockJS(wsUrl),
      reconnectDelay: this.reconnectDelay,
      heartbeatIncoming: 4000,
      heartbeatOutgoing: 4000,
      
      onConnect: () => {
        console.log('✅ STOMP connected');
        this.connectionStatusSubject.next(true);
        this.reconnectAttempts = 0;
      },

      onStompError: (error) => {
        console.error('❌ STOMP error:', error);
        this.connectionStatusSubject.next(false);
      },

      onDisconnect: () => {
        console.log('⚠️ STOMP disconnected');
        this.connectionStatusSubject.next(false);
        this.attemptReconnect();
      }
    });

    this.client.activate();
  }

  public subscribeToChannel(channel: string): Observable<any> {
    if (!this.isBrowser) {
      console.log('⏸️ Skipping WebSocket subscription (SSR context)');
      return this.message$;
    }

    // Skip if already subscribed to this channel
    if (this.activeSubscriptions.has(channel)) {
      console.log('✅ Already subscribed to channel:', channel);
      return this.message$;
    }

    // Wait for connection if not connected yet
    if (!this.client || !this.client.connected) {
      console.log('⏳ Waiting for connection before subscribing to:', channel);
      setTimeout(() => this.subscribeToChannel(channel), 1000);
      return this.message$;
    }

    console.log('📡 Subscribing to channel:', channel);
    const subscription = this.client.subscribe(channel, (message) => {
      try {
        const data = JSON.parse(message.body);
        console.log('📨 Message received from', channel, ':', data);
        this.messageSubject.next(data);
      } catch (e) {
        console.error('❌ Error parsing message:', e);
      }
    });

    this.activeSubscriptions.set(channel, subscription);
    return this.message$;
  }

  private attemptReconnect(): void {
    if (this.reconnectAttempts < this.maxReconnectAttempts) {
      this.reconnectAttempts++;
      console.log(`🔄 Reconnect attempt ${this.reconnectAttempts}/${this.maxReconnectAttempts} in ${this.reconnectDelay}ms`);
      setTimeout(() => this.connect(), this.reconnectDelay);
    } else {
      console.error('❌ Max reconnect attempts reached');
      this.connectionStatusSubject.next(false);
    }
  }

  /**
   * Check if connection is alive and attempt to reconnect if dead
   */
  public checkConnectionHealth(): void {
    if (!this.isBrowser) return;

    if (this.client && !this.client.connected) {
      console.warn('⚠️ WebSocket connection detected as dead, attempting reconnect...');
      this.reconnectAttempts = 0; // Reset attempts for manual health check
      this.connect();
    }
  }

  /**
   * Reset connection attempts and force reconnect
   */
  public forceReconnect(): void {
    if (!this.isBrowser) return;

    console.log('🔄 Forcing WebSocket reconnection...');
    this.disconnect();
    this.reconnectAttempts = 0;
    setTimeout(() => this.connect(), 1000);
  }

  // send(data: any): void {
  //   if (!this.isBrowser) return;
    
  //   if (!this.client || !this.client.connected) {
  //     console.warn('⚠️ STOMP not connected, cannot send:', data.type || 'unknown');
  //     return;
  //   }

  //   console.log('📤 Sending STOMP message:', data.type || 'unknown');
  //   this.client.publish({
  //     destination: '/app/message',
  //     body: JSON.stringify(data)
  //   });
  // }

  subscribe(channel: string, callback: (data: any) => void): Observable<any> {
    if (!this.isBrowser) return this.message$;
    
    return this.subscribeToChannel(channel);
  }

  disconnect(): void {
    if (!this.isBrowser) return;
    
    console.log('🔌 Closing STOMP connection...');
    if (this.client && this.client.connected) {
      this.client.deactivate();
    }
    this.connectionStatusSubject.next(false);
  }

  isConnected(): boolean {
    return this.isBrowser && this.client ? this.client.connected : false;
  }
}
