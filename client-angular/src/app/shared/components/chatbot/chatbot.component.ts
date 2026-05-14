import {
  AfterViewChecked,
  ChangeDetectionStrategy,
  ChangeDetectorRef,
  Component,
  ElementRef,
  HostListener,
  Inject,
  OnDestroy,
  OnInit,
  PLATFORM_ID,
  ViewChild,
} from '@angular/core';
import { CommonModule, DatePipe, isPlatformBrowser } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Subject } from 'rxjs';
import { takeUntil } from 'rxjs/operators';

import { AuthService } from '../../../core/services/auth.service';
import { ChatbotService } from './chatbot.service';
import {
  ChatMessage,
  ChatbotPanelState,
  SuggestedPrompt,
} from './chatbot.models';

/**
 * NEXORA Floating AI Chat Assistant.
 *
 * A reusable, standalone widget that lives at the bottom-right of the
 * viewport. It exposes:
 *   - a circular floating action button (FAB) that opens / closes the panel
 *   - a chat panel with header, welcome state, suggested prompts, message
 *     thread, typing indicator and a rich input bar.
 *
 * State is owned by `ChatbotService` so the conversation persists while
 * the component is alive and can later be wired to a real backend.
 */
@Component({
  selector: 'app-chatbot',
  standalone: true,
  imports: [CommonModule, FormsModule, DatePipe],
  templateUrl: './chatbot.component.html',
  styleUrls: ['./chatbot.component.scss'],
  changeDetection: ChangeDetectionStrategy.Default,
})
export class ChatbotComponent implements OnInit, OnDestroy, AfterViewChecked {
  @ViewChild('messagesContainer') private messagesContainer?: ElementRef<HTMLElement>;
  @ViewChild('inputArea') private inputArea?: ElementRef<HTMLTextAreaElement>;

  /** Panel visibility / size mode. */
  panelState: ChatbotPanelState = 'closed';

  /** Local mirror of service state (kept in sync via subscriptions). */
  messages: ChatMessage[] = [];
  isTyping = false;

  /** v-model for the textarea. */
  draft = '';

  /** Display name used in the welcome section. */
  username = 'there';

  readonly suggestedPrompts: SuggestedPrompt[] = [
    {
      label: 'How to start a new chat?',
      prompt: 'How do I start a new chat with someone in NEXORA?',
    },
    {
      label: 'How to create a group?',
      prompt: 'How can I create and manage a group in NEXORA?',
    },
    {
      label: 'What are NEXORA’s key features?',
      prompt: 'What are the key features of NEXORA?',
    },
  ];

  private readonly destroy$ = new Subject<void>();
  private shouldScrollToBottom = false;
  private readonly isBrowser: boolean;

  constructor(
    private chatbot: ChatbotService,
    private auth: AuthService,
    private cdr: ChangeDetectorRef,
    private host: ElementRef<HTMLElement>,
    @Inject(PLATFORM_ID) platformId: object
  ) {
    this.isBrowser = isPlatformBrowser(platformId);
  }

  // ---------------------------------------------------------------------------
  // Lifecycle
  // ---------------------------------------------------------------------------

  ngOnInit(): void {
    this.chatbot.messages$
      .pipe(takeUntil(this.destroy$))
      .subscribe((messages) => {
        const grew = messages.length > this.messages.length;
        this.messages = messages;
        if (grew) {
          this.shouldScrollToBottom = true;
        }
        this.cdr.markForCheck();
      });

    this.chatbot.typing$
      .pipe(takeUntil(this.destroy$))
      .subscribe((typing) => {
        this.isTyping = typing;
        if (typing) {
          this.shouldScrollToBottom = true;
        }
        this.cdr.markForCheck();
      });

    this.auth.currentUser$
      .pipe(takeUntil(this.destroy$))
      .subscribe((user: any) => {
        this.username =
          user?.firstName || user?.username || user?.email?.split('@')[0] || 'there';
        this.cdr.markForCheck();
      });
  }

  ngAfterViewChecked(): void {
    if (this.shouldScrollToBottom) {
      this.scrollToBottom();
      this.shouldScrollToBottom = false;
    }
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }

  // ---------------------------------------------------------------------------
  // Open / close / size
  // ---------------------------------------------------------------------------

  get isOpen(): boolean {
    return this.panelState === 'open' || this.panelState === 'expanded';
  }

  get isMinimized(): boolean {
    return this.panelState === 'minimized';
  }

  get isExpanded(): boolean {
    return this.panelState === 'expanded';
  }

  toggle(): void {
    this.panelState = this.panelState === 'closed' ? 'open' : 'closed';
    if (this.isOpen) {
      this.shouldScrollToBottom = true;
      // Defer focus until the panel finishes its enter animation.
      setTimeout(() => this.inputArea?.nativeElement?.focus(), 220);
    }
  }

  close(): void {
    this.panelState = 'closed';
  }

  minimize(): void {
    this.panelState = this.panelState === 'minimized' ? 'open' : 'minimized';
  }

  toggleExpand(): void {
    this.panelState = this.panelState === 'expanded' ? 'open' : 'expanded';
    this.shouldScrollToBottom = true;
  }

  // ---------------------------------------------------------------------------
  // Sending
  // ---------------------------------------------------------------------------

  /** Send the current draft. Returns silently if empty / typing. */
  send(): void {
    const text = this.draft.trim();
    if (!text || this.isTyping) {
      return;
    }
    this.draft = '';
    this.autoSizeTextarea();

    this.chatbot
      .sendMessage(text)
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        error: (err) => console.error('Chatbot send failed:', err),
      });
  }

  /** Click on a suggested prompt – sends it immediately. */
  usePrompt(prompt: SuggestedPrompt): void {
    if (this.isTyping) return;
    this.draft = prompt.prompt;
    this.send();
  }

  /** Keyboard handler for the textarea. */
  onInputKeydown(event: KeyboardEvent): void {
    if (event.key === 'Enter' && !event.shiftKey) {
      event.preventDefault();
      this.send();
    }
  }

  /** Auto-grow textarea up to a reasonable max height. */
  autoSizeTextarea(): void {
    const el = this.inputArea?.nativeElement;
    if (!el) return;
    el.style.height = 'auto';
    el.style.height = `${Math.min(el.scrollHeight, 140)}px`;
  }

  // ---------------------------------------------------------------------------
  // Global key + outside-click handling
  // ---------------------------------------------------------------------------

  @HostListener('document:keydown.escape')
  onEscape(): void {
    if (this.isOpen || this.isMinimized) {
      this.close();
    }
  }

  @HostListener('document:mousedown', ['$event'])
  onDocumentClick(event: MouseEvent): void {
    if (!this.isBrowser) return;
    if (this.panelState === 'closed') return;
    const target = event.target as Node | null;
    if (target && this.host.nativeElement.contains(target)) {
      return;
    }
    this.close();
  }

  // ---------------------------------------------------------------------------
  // Helpers
  // ---------------------------------------------------------------------------

  trackById = (_: number, m: ChatMessage): string => m.id;

  /**
   * Lightweight markdown -> safe HTML renderer for assistant messages.
   * Supports: bold, italic, inline code, code blocks, blockquotes,
   * unordered list items, line breaks. All input is HTML-escaped first
   * to prevent XSS.
   */
  renderMarkdown(text: string): string {
    const escaped = this.escapeHtml(text);

    // Code blocks ```...```
    let html = escaped.replace(/```([\s\S]*?)```/g, (_m, code) => {
      return `<pre class="md-code"><code>${code.trim()}</code></pre>`;
    });

    // Inline code `...`
    html = html.replace(/`([^`\n]+)`/g, '<code class="md-inline-code">$1</code>');

    // Bold **...**
    html = html.replace(/\*\*([^*]+)\*\*/g, '<strong>$1</strong>');

    // Italic _..._
    html = html.replace(/(^|\s)_([^_\n]+)_/g, '$1<em>$2</em>');

    // Blockquotes > ...
    html = html.replace(/(^|\n)&gt;\s?([^\n]+)/g, '$1<blockquote>$2</blockquote>');

    // Unordered list items: lines starting with "- "
    html = html.replace(/(^|\n)-\s+([^\n]+)/g, '$1<li>$2</li>');
    html = html.replace(/(<li>[\s\S]*?<\/li>)(?!\s*<li>)/g, '<ul>$1</ul>');

    // Line breaks
    html = html.replace(/\n/g, '<br/>');

    return html;
  }

  private escapeHtml(text: string): string {
    return text
      .replace(/&/g, '&amp;')
      .replace(/</g, '&lt;')
      .replace(/>/g, '&gt;')
      .replace(/"/g, '&quot;')
      .replace(/'/g, '&#39;');
  }

  private scrollToBottom(): void {
    const el = this.messagesContainer?.nativeElement;
    if (!el) return;
    el.scrollTo({ top: el.scrollHeight, behavior: 'smooth' });
  }
}
