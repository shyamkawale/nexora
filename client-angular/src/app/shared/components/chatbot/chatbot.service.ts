import { Injectable } from '@angular/core';
import { HttpClient, HttpErrorResponse } from '@angular/common/http';
import { BehaviorSubject, Observable, throwError } from 'rxjs';
import { catchError, map } from 'rxjs/operators';
import { environment } from '../../../../environments/environment';
import { ChatMessage } from './chatbot.models';

/**
 * Backend payload for `POST /api/v1/chat`.
 * Mirrors `com.svk.nexora_be.dto.request.ChatRequest`.
 */
interface ChatApiRequest {
  message: string;
}

/**
 * Backend payload returned by `POST /api/v1/chat`.
 * Mirrors `com.svk.nexora_be.dto.response.ChatResponse`.
 */
interface ChatApiResponse {
  answer: string;
}

/**
 * ChatbotService
 *
 * Owns the conversation state for the floating NEXORA AI Assistant
 * and talks to the Spring Boot `/api/v1/chat` endpoint, which proxies
 * to OpenAI server-side.
 *
 * The endpoint accepts `{ message }` and returns `{ answer }` (JSON).
 * We use `HttpClient` directly rather than the shared `HttpService`
 * because `HttpService.post` unwraps a `data` envelope, which this
 * endpoint doesn't use. The global `AuthInterceptor` still attaches
 * the bearer token automatically.
 */
@Injectable({ providedIn: 'root' })
export class ChatbotService {
  /** Endpoint that proxies to the OpenAI service on the backend. */
  private readonly chatEndpoint = `${environment.serverUrl}/api/v1/chat`;

  private readonly messagesSubject = new BehaviorSubject<ChatMessage[]>([]);
  /** Stream of all messages in the current conversation. */
  readonly messages$ = this.messagesSubject.asObservable();

  private readonly typingSubject = new BehaviorSubject<boolean>(false);
  /** True while the assistant is "thinking" / typing. */
  readonly typing$ = this.typingSubject.asObservable();

  constructor(private http: HttpClient) {}

  /** Current snapshot of messages. */
  get messages(): ChatMessage[] {
    return this.messagesSubject.value;
  }

  /**
   * Add a user message and request an assistant reply from the backend.
   * Returns an Observable that emits the assistant message when the
   * reply has been appended to the thread.
   */
  sendMessage(content: string): Observable<ChatMessage> {
    const trimmed = (content ?? '').trim();
    if (!trimmed) {
      return throwError(() => new Error('Empty message'));
    }

    const userMsg: ChatMessage = {
      id: this.generateId(),
      role: 'user',
      content: trimmed,
      timestamp: Date.now(),
    };
    this.appendMessage(userMsg);

    this.typingSubject.next(true);

    return this.fetchAssistantReply(trimmed).pipe(
      map((reply) => {
        const assistantMsg: ChatMessage = {
          id: this.generateId(),
          role: 'assistant',
          content: reply,
          timestamp: Date.now(),
        };
        this.appendMessage(assistantMsg);
        this.typingSubject.next(false);
        return assistantMsg;
      }),
      catchError((err: HttpErrorResponse | Error) => {
        // Surface a friendly assistant message on failure so the UI
        // doesn't get stuck on the typing indicator.
        const fallback: ChatMessage = {
          id: this.generateId(),
          role: 'assistant',
          content: this.buildErrorReply(err),
          timestamp: Date.now(),
        };
        this.appendMessage(fallback);
        this.typingSubject.next(false);
        console.error('Chatbot API error:', err);
        return throwError(() => err);
      })
    );
  }

  /** Clear the conversation. */
  reset(): void {
    this.messagesSubject.next([]);
    this.typingSubject.next(false);
  }

  // ---------------------------------------------------------------------------
  // Internals
  // ---------------------------------------------------------------------------

  private appendMessage(msg: ChatMessage): void {
    this.messagesSubject.next([...this.messagesSubject.value, msg]);
  }

  /**
   * Calls `POST /api/v1/chat` with `{ message }` and returns the
   * `answer` field from the JSON response.
   */
  private fetchAssistantReply(userText: string): Observable<string> {
    const body: ChatApiRequest = { message: userText };
    return this.http
      .post<ChatApiResponse>(this.chatEndpoint, body)
      .pipe(map((res) => (res?.answer ?? '').toString()));
  }

  private buildErrorReply(err: HttpErrorResponse | Error): string {
    if (err instanceof HttpErrorResponse) {
      if (err.status === 0) {
        return `⚠️ I couldn't reach the server. Please check your connection and try again.`;
      }
      if (err.status === 401 || err.status === 403) {
        return `⚠️ Your session looks expired. Please sign in again to continue chatting.`;
      }
      if (err.status >= 500) {
        return `⚠️ The assistant ran into a server error (status ${err.status}). Please try again in a moment.`;
      }
      return `⚠️ Request failed with status ${err.status}. Please try again.`;
    }
    return `⚠️ Something went wrong while contacting the assistant. Please try again.`;
  }

  private generateId(): string {
    return `${Date.now().toString(36)}-${Math.random().toString(36).slice(2, 8)}`;
  }
}
