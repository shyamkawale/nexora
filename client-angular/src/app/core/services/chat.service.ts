import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { HttpService } from './http.service';

export interface DirectMessageChat {
  publicId: string;
  user1: any;
  user2: any;
  createdAt: string;
  updatedAt: string;
}

export interface DirectMessage {
  publicId: string;
  chat: DirectMessageChat;
  sender: any;
  message: string;
  isRead: boolean;
  containsMedia: boolean;
  createdAt: string;
  updatedAt: string;
}

export interface DirectMessageResponse {
  publicId: string;
  message: string;
  sender: any;
  isRead: boolean;
  containsMedia: boolean;
  createdAt: string;
}

export interface GroupChat {
  publicId: string;
  groupName: string;
  description?: string;
  createdBy: any;
  members: any[];
  isActive: boolean;
  createdAt: string;
}

export interface GroupMessage {
  publicId: string;
  groupChatId: string;
  sender: any;
  message: string;
  createdAt: string;
}

export interface GroupChatMessage {
  publicId: string;
  group: GroupChat;
  sender: any;
  message: string;
  messageStatus: 'SENT' | 'DELIVERED' | 'READ';
  containsMedia: boolean;
  createdAt: string;
  updatedAt: string;
}

@Injectable({
  providedIn: 'root'
})
export class ChatService {
  constructor(private httpService: HttpService) {}

  // Direct Messages
  getOrCreateDirectChat(otherUserId: string): Observable<DirectMessageChat> {
    console.log('📞 Getting or creating direct chat with user:', otherUserId);
    return this.httpService.get<DirectMessageChat>(`/api/v1/direct-messages/chats/${otherUserId}`);
  }

  getDirectMessages(chatId: string, page: number = 0, size: number = 20): Observable<any> {
    console.log('💬 Fetching direct messages for chat:', chatId);
    return this.httpService.get(`/api/v1/direct-messages/${chatId}?page=${page}&size=${size}`);
  }

  getUserDirectChats(): Observable<DirectMessageChat[]> {
    console.log('📋 Fetching user direct chats');
    return this.httpService.get<DirectMessageChat[]>('/api/v1/direct-messages/user/chats');
  }

  sendDirectMessage(chatId: string, message: string): Observable<DirectMessageResponse> {
    console.log('📤 Sending direct message to chat:', chatId);
    return this.httpService.post<DirectMessageResponse>('/api/v1/direct-messages', {
      chatId,
      message
    });
  }

  // Group Chat
  createGroupChat(request: { groupName: string; description?: string; memberPublicIds: string[] }): Observable<GroupChat> {
    console.log('👥 Creating group chat:', request.groupName);
    return this.httpService.post<GroupChat>('/api/v1/group-chats', request);
  }

  getUserGroupChats(): Observable<GroupChat[]> {
    console.log('👥 Fetching user group chats');
    return this.httpService.get<GroupChat[]>('/api/v1/group-chats');
  }

  getGroupChatDetails(chatId: string): Observable<GroupChat> {
    console.log('👥 Fetching group chat details:', chatId);
    return this.httpService.get<GroupChat>(`/api/v1/group-chats/${chatId}`);
  }

  getGroupMessages(groupChatId: string, page: number = 0, size: number = 20): Observable<any> {
    console.log('💬 Fetching group messages:', groupChatId);
    return this.httpService.get(`/api/v1/group-chats/${groupChatId}/messages?page=${page}&size=${size}`);
  }

  sendGroupMessage(chatId: string, message: string): Observable<GroupMessage> {
    console.log('📤 Sending group message to chat:', chatId);
    return this.httpService.post<GroupMessage>(`/api/v1/group-chats/${chatId}/messages`, {
      chatId,
      message
    });
  }

  addGroupMember(groupId: string, userId: string): Observable<any> {
    return this.httpService.post(`/api/v1/groups/${groupId}/members/${userId}`, {});
  }

  removeGroupMember(groupId: string, userId: string): Observable<any> {
    return this.httpService.delete(`/api/v1/groups/${groupId}/members/${userId}`);
  }

  deleteMessage(messageId: string): Observable<any> {
    return this.httpService.delete(`/api/v1/group-chats/messages/${messageId}`);
  }

  uploadGroupProfilePicture(groupId: string, file: File): Observable<any> {
    const formData = new FormData();
    formData.append('file', file);
    return this.httpService.postFormData(`/api/v1/groups/${groupId}/profile-picture`, formData);
  }

  getAllUsers(): Observable<any[]> {
    console.log('👥 Fetching all users for group member selection');
    return this.httpService.get<any[]>('/api/v1/users');
  }
}
