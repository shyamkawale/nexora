/**
 * Models / interfaces for the NEXORA AI Chat Assistant.
 */

export type ChatRole = 'user' | 'assistant' | 'system';

export interface ChatMessage {
  /** Stable id (uuid / timestamp) used as trackBy key. */
  id: string;
  role: ChatRole;
  /** Raw markdown / text content. */
  content: string;
  /** Epoch ms when message was created. */
  timestamp: number;
  /** Optional pending flag (used while streaming responses in the future). */
  pending?: boolean;
}

export interface SuggestedPrompt {
  label: string;
  prompt: string;
}

export type ChatbotPanelState = 'closed' | 'open' | 'minimized' | 'expanded';
