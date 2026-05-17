import { Component, OnInit, OnDestroy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RouterModule, Router } from '@angular/router';
import { MatIconModule } from '@angular/material/icon';
import { LoaderComponent } from '../../shared/components/loader.component';
import { ChatService, DirectChat, DirectChatMessage, GroupChat, GroupChatMessage } from '../../core/services/chat.service';
import { WebSocketService } from '../../core/services/websocket.service';
import { AuthService } from '../../core/services/auth.service';
import { PresenceService } from '../../core/services/presence.service';
import { FileUploadService, FileUploadProgress } from '../../core/services/file-upload.service';
import { Subject } from 'rxjs';
import { takeUntil } from 'rxjs/operators';

@Component({
  selector: 'app-chat',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterModule, LoaderComponent, MatIconModule],
  templateUrl: './chat.component.html',
  styleUrls: ['./chat.component.scss']
})
export class ChatComponent implements OnInit, OnDestroy {
  loadingChats = true;
  loadingMessages = false;
  userChats: (DirectChat | GroupChat)[] = [];
  messages: any[] = [];
  selectedChat: DirectChat | GroupChat | null = null;
  selectedChatId: string | null = null;
  selectedChatType: 'direct' | 'group' | null = null;
  newMessage = '';
  currentUserId: string | null = null;
  chatType: 'all' | 'direct' | 'group' = 'all'; // For tab filtering
  directChats: DirectChat[] = [];
  groupChats: GroupChat[] = [];
  private destroy$ = new Subject<void>();

  // Group creation modal
  showCreateGroupModal = false;
  showGroupDetails = false;
  createGroupForm = {
    groupName: '',
    description: '',
    selectedMembers: [] as any[]
  };
  memberSearchQuery = '';
  memberSearchResults: any[] = [];
  allUsers: any[] = [];

  // File upload properties
  previewFiles: Array<{ name: string; size: number; preview?: string; file?: File }> = [];
  uploadProgress: FileUploadProgress | null = null;
  
  // Separate caches for viewing and downloading presigned URLs
  presignedUrlCache = new Map<string, string>(); // For viewing (download=false)
  presignedDownloadCache = new Map<string, string>(); // For downloading (download=true)

  // Helper properties for template binding
  get groupDetails() {
    return this.selectedChat as GroupChat;
  }

  constructor(
    private chatService: ChatService,
    private webSocketService: WebSocketService,
    private authService: AuthService,
    private presenceService: PresenceService,
    private fileUploadService: FileUploadService,
    private router: Router
  ) {
    // Get current user ID
    this.authService.currentUser$
      .pipe(takeUntil(this.destroy$))
      .subscribe(user => {
        this.currentUserId = this.resolveEntityId(user);
        console.log('👤 Current user ID:', this.currentUserId);
      });

    // Get chatId from navigation state if it exists
    const navigation = this.router.getCurrentNavigation();
    if (navigation?.extras?.state?.['chatId']) {
      this.selectedChatId = navigation.extras.state['chatId'];
      console.log('📞 Opening chat with ID:', this.selectedChatId);
    }
  }

  ngOnInit(): void {
    console.log('💬 Chat component initialized');
    this.loadUserChats();
    this.subscribeToWebSocketMessages();
  }

  private loadUserChats(): void {
    console.log('🔄 Loading user chats...');
    this.loadingChats = true;
    
    // Load direct chats
    this.chatService.getUserDirectChats()
      .pipe(takeUntil(this.destroy$))
      .subscribe(
        chats => {
          console.log('✅ Direct chats loaded:', chats);
          this.directChats = chats || [];
          this.updateChatList();
        },
        error => {
          console.error('❌ Error loading direct chats:', error);
          this.loadingChats = false;
        }
      );

    // Load group chats
    this.chatService.getUserGroupChats()
      .pipe(takeUntil(this.destroy$))
      .subscribe(
        chats => {
          console.log('✅ Group chats loaded:', chats);
          this.groupChats = chats || [];
          this.updateChatList();
        },
        error => {
          console.error('❌ Error loading group chats:', error);
          this.loadingChats = false;
        }
      );
  }

  refreshChats(): void {
    if (this.loadingChats) return;
    this.loadUserChats();
  }

  private updateChatList(): void {
    switch (this.chatType) {
      case 'direct':
        this.userChats = this.directChats;
        break;
      case 'group':
        this.userChats = this.groupChats;
        break;
      default:
        this.userChats = [...this.directChats, ...this.groupChats];
    }
    this.loadingChats = false;

    // If we have a selected chat ID, find and select it
    if (this.selectedChatId) {
      const chat = this.userChats.find(c => c.publicId === this.selectedChatId);
      if (chat) {
        this.selectChat(chat);
      }
    }
  }

  changeChatType(type: 'all' | 'direct' | 'group'): void {
    this.chatType = type;
    this.updateChatList();
  }

  selectChat(chat: DirectChat | GroupChat): void {
    console.log('📞 Selecting chat:', chat.publicId);
    this.selectedChat = chat;
    this.selectedChatId = chat.publicId;
    this.messages = [];
    this.showGroupDetails = false; // Close details panel when switching chats
    
    // Check WebSocket health before subscribing
    this.webSocketService.checkConnectionHealth();
    
    // Determine chat type
    if ('user1' in chat) {
      // It's a direct message chat
      this.selectedChatType = 'direct';
      this.webSocketService.subscribeToChannel(`/topic/messages/${chat.publicId}`);
    } else {
      // It's a group chat
      this.selectedChatType = 'group';
      this.webSocketService.subscribeToChannel(`/topic/group-messages/${chat.publicId}`);
    }
    
    this.loadMessages();
  }

  private loadMessages(): void {
    if (!this.selectedChat) return;

    console.log('💬 Loading messages for chat:', this.selectedChat.publicId);
    this.loadingMessages = true;

    // Load messages based on chat type
    const loadObservable = this.selectedChatType === 'direct'
      ? this.chatService.getDirectMessages(this.selectedChat.publicId, 0, 50)
      : this.chatService.getGroupMessages(this.selectedChat.publicId, 0, 50);

    loadObservable
      .pipe(takeUntil(this.destroy$))
      .subscribe(
        response => {
          console.log('✅ Messages loaded:', response);
          
          // Handle if response is wrapped
          let msgList = Array.isArray(response) ? response : (response?.data || response?.content || []);
          
          // Sort messages by createdAt in ascending order (oldest first, newest last)
          this.messages = (msgList || []).sort((a: any, b: any) => {
            const dateA = new Date(a.createdAt).getTime();
            const dateB = new Date(b.createdAt).getTime();
            return dateA - dateB;
          });
          
          // Preload presigned URLs for all files in messages
          this.messages.forEach(msg => {
            const files = this.getMessageFiles(msg);
            if (files.length > 0) {
              this.preloadPresignedUrls(files);
            }
          });
          
          this.loadingMessages = false;
        },
        error => {
          console.error('❌ Error loading messages:', error);
          this.loadingMessages = false;
        }
      );
  }

  /**
   * Handle file selection from input
   */
  onFileSelected(event: Event): void {
    const input = event.target as HTMLInputElement;
    if (!input.files || input.files.length === 0) return;

    console.log('📁 Files selected:', input.files.length);

    for (let i = 0; i < input.files.length; i++) {
      const file = input.files[i];

      // Validate file
      const validation = this.fileUploadService.validateFile(file, 50, [
        'image/jpeg', 'image/png', 'image/gif', 'image/webp',
        'application/pdf',
        'application/msword',
        'application/vnd.openxmlformats-officedocument.wordprocessingml.document',
        'application/vnd.ms-excel',
        'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet'
      ]);

      if (!validation.valid) {
        alert(`❌ ${file.name}: ${validation.error}`);
        continue;
      }

      // Create preview
      const reader = new FileReader();
      reader.onload = (e) => {
        this.previewFiles.push({
          name: file.name,
          size: file.size,
          file: file,
          preview: this.isImageFile(file.name) ? e.target?.result as string : undefined
        });
      };
      reader.readAsDataURL(file);
    }

    // Reset file input
    input.value = '';
  }

  /**
   * Remove file from preview list
   */
  removePreviewFile(index: number): void {
    this.previewFiles.splice(index, 1);
  }

  /**
   * Check if file is an image
   */
  isImageFile(fileName: string): boolean {
    const imageExtensions = ['.jpg', '.jpeg', '.png', '.gif', '.webp', '.avif'];
    return imageExtensions.some(ext => fileName.toLowerCase().endsWith(ext));
  }

  /**
   * Format file size for display
   */
  formatFileSize(bytes: number): string {
    if (bytes === 0) return '0 Bytes';
    const k = 1024;
    const sizes = ['Bytes', 'KB', 'MB', 'GB'];
    const i = Math.floor(Math.log(bytes) / Math.log(k));
    return Math.round((bytes / Math.pow(k, i)) * 100) / 100 + ' ' + sizes[i];
  }

  /**
   * Extract message text from message (excludes file URLs)
   */
  getMessageText(msg: any): string {
    if (!msg.message) return '';
    // Remove file URLs from message text
    return msg.message.replace(/https?:\/\/[^\s)]+\.s3[^\s)]*\.(jpg|jpeg|png|gif|webp|pdf|doc|docx|xls|xlsx)[^\s)]*/gi, '').trim();
  }

  /**
   * Extract file URLs from message
   */
  getMessageFiles(msg: any): string[] {
    if (!msg.message) return [];
    // Extract S3 file URLs
    const urls = msg.message.match(/https?:\/\/[^\s)]+\.s3[^\s)]*\.(jpg|jpeg|png|gif|webp|pdf|doc|docx|xls|xlsx)[^\s)]*/gi) || [];
    return urls;
  }

  /**
   * Check if URL is an image
   */
  isImageUrl(url: string): boolean {
    const imageExtensions = ['.jpg', '.jpeg', '.png', '.webp'];
    return imageExtensions.some(ext => url.toLowerCase().includes(ext));
  }

  /**
   * Check if URL is a PDF
   */
  isPdfUrl(url: string): boolean {
    return url.toLowerCase().includes('.pdf');
  }

  /**
   * Check if file type is supported for display
   */
  isSupportedFileType(url: string): boolean {
    return this.isImageUrl(url) || this.isPdfUrl(url);
  }

  /**
   * Extract file key from S3 URL
   */
  extractFileKey(s3Url: string): string {
    try {
      // S3 URL format: https://bucket.s3.region.amazonaws.com/key?query-params
      const urlParts = new URL(s3Url);
      const pathname = urlParts.pathname;
      // Remove leading slash: /key → key
      return pathname.startsWith('/') ? pathname.substring(1) : pathname;
    } catch (e) {
      console.error('❌ Error extracting file key:', e);
      return s3Url;
    }
  }

  /**
   * Extract file name from S3 URL
   */
  extractFileName(url: string): string {
    try {
      // Extract filename from URL path
      const parts = url.split('/');
      const fileWithQuery = parts[parts.length - 1];
      const fileName = fileWithQuery.split('?')[0];
      // Decode URL encoding
      return decodeURIComponent(fileName);
    } catch (e) {
      return 'File';
    }
  }

  /**
   * Open file preview in new tab (for viewing)
   */
  openFilePreview(fileUrl: string): void {
    const a = document.createElement('a');
    a.href = fileUrl;
    a.target = '_blank';
    a.click();
  }

  /**
   * Download file using presigned URL
   */
  downloadFile(s3Url: string): void {
    console.log('⬇️ Downloading file:', s3Url);
    
    try {
      const fileKey = this.extractFileKey(s3Url);
      
      // Get presigned URL from DOWNLOAD cache or fetch it (download=true for download mode)
      const presignedUrl = this.presignedDownloadCache.get(s3Url);
      
      if (presignedUrl) {
        this.download(presignedUrl);
      } else {
        // Fetch presigned URL with download=true (to force download)
        this.fileUploadService.getDownloadUrl(fileKey, true)
          .pipe(takeUntil(this.destroy$))
          .subscribe({
            next: (response: any) => {
              if (response?.presignedUrl) {
                // Cache the download URL
                this.presignedDownloadCache.set(s3Url, response.presignedUrl);
                this.download(response.presignedUrl);
              }
            },
            error: (err: any) => {
              console.error('❌ Error getting download URL:', err);
              alert('Failed to download file. Please try again.');
            }
          });
      }
    } catch (e) {
      console.error('❌ Error processing download:', e);
      alert('Error processing file download.');
    }
  }

  /**
   * Helper to download from URL (with download attribute)
   */
  private download(url: string): void {
    const a = document.createElement('a');
    a.href = url;
    a.download = '';
    a.click();
  }

  /**
   * Get or fetch presigned URL for file (cached) - for viewing
   */
  getPresignedUrl(s3Url: string): string | null {
    // Return from cache if available
    if (this.presignedUrlCache.has(s3Url)) {
      return this.presignedUrlCache.get(s3Url) || null;
    }
    
    // Fetch and cache if not available (async operation)
    const fileKey = this.extractFileKey(s3Url);
    this.fileUploadService.getDownloadUrl(fileKey, false)
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: (response: any) => {
          if (response?.presignedUrl) {
            // Cache the presigned URL for viewing
            this.presignedUrlCache.set(s3Url, response.presignedUrl);
            console.log('✅ Cached presigned URL');
          }
        },
        error: (err: any) => {
          console.error('❌ Error fetching presigned URL:', err);
        }
      });
    
    return null; // Return null initially, will be populated by cache on next access
  }

  /**
   * Preload presigned URLs for all files in a message (for viewing)
   */
  preloadPresignedUrls(files: string[]): void {
    files.forEach(fileUrl => {
      if (!this.presignedUrlCache.has(fileUrl)) {
        const fileKey = this.extractFileKey(fileUrl);
        this.fileUploadService.getDownloadUrl(fileKey, false)
          .pipe(takeUntil(this.destroy$))
          .subscribe({
            next: (response: any) => {
              if (response?.presignedUrl) {
                // Cache the viewing URL
                this.presignedUrlCache.set(fileUrl, response.presignedUrl);
                console.log('✅ Preloaded presigned URL for:', this.extractFileName(fileUrl));
              }
            },
            error: (err: any) => {
              console.error('❌ Error preloading presigned URL:', err);
            }
          });
      }
    });
  }

  /**
   * Open image preview using presigned URL (opens in new tab for viewing)
   */
  openImagePreview(s3Url: string): void {
    console.log('👁️ Opening file in new tab:', s3Url);
    
    try {
      const fileKey = this.extractFileKey(s3Url);
      
      // Get presigned URL from cache or fetch it (download=false for inline viewing)
      const presignedUrl = this.presignedUrlCache.get(s3Url);
      
      if (presignedUrl) {
        this.openInNewTab(presignedUrl);
      } else {
        // Fetch presigned URL with download=false (for inline viewing)
        this.fileUploadService.getDownloadUrl(fileKey, false)
          .pipe(takeUntil(this.destroy$))
          .subscribe({
            next: (response: any) => {
              if (response?.presignedUrl) {
                this.openInNewTab(response.presignedUrl);
              }
            },
            error: (err: any) => {
              console.error('❌ Error getting presigned URL:', err);
              alert('Failed to open file. Please try again.');
            }
          });
      }
    } catch (e) {
      console.error('❌ Error processing file:', e);
      alert('Error opening file.');
    }
  }

  /**
   * Helper to open URL in new tab (for viewing)
   */
  private openInNewTab(url: string): void {
    const a = document.createElement('a');
    a.href = url;
    a.target = '_blank';
    a.click();
  }

  /**
   * Upload file to S3 and get URL
   */
  private uploadFile(file: File): Promise<string> {
    return new Promise((resolve, reject) => {
      this.fileUploadService.uploadFile(file, (progress) => {
        this.uploadProgress = progress;
        console.log(`📤 Upload progress:`, progress);
      }).subscribe({
        next: (fileUrl) => {
          console.log('✅ File uploaded successfully:', fileUrl);
          resolve(fileUrl);
        },
        error: (err) => {
          console.error('❌ File upload failed:', err);
          reject(err);
        }
      });
    });
  }

  /**
   * Upload all preview files to S3
   */
  private async uploadAllFiles(): Promise<string[]> {
    if (this.previewFiles.length === 0) {
      return [];
    }

    console.log('📤 Starting file uploads...');
    const uploadPromises = this.previewFiles.map(fileData => {
      if (!fileData.file) {
        return Promise.reject(new Error('File object missing'));
      }
      return this.uploadFile(fileData.file);
    });

    try {
      const fileUrls = await Promise.all(uploadPromises);
      console.log('✅ All files uploaded:', fileUrls);
      return fileUrls;
    } catch (error) {
      console.error('❌ Error uploading files:', error);
      throw error;
    }
  }

  sendMessage(): void {
    if ((!this.newMessage?.trim() && this.previewFiles.length === 0) || !this.selectedChat) {
      console.warn('⚠️ Cannot send: no chat selected or message/files empty');
      return;
    }

    const message = this.newMessage;
    const chatId = this.selectedChat.publicId;
    const hasFiles = this.previewFiles.length > 0;

    console.log('📤 Preparing message:', { message, hasFiles, fileCount: this.previewFiles.length });

    // If there are files, upload them first
    if (hasFiles) {
      this.uploadAllFiles()
        .then((fileUrls) => {
          console.log('✅ Files uploaded, sending message with attachments:', fileUrls);
          this.sendMessageToChat(chatId, message, fileUrls);
        })
        .catch((error) => {
          console.error('❌ File upload failed:', error);
          alert('Failed to upload files. Please try again.');
          this.newMessage = message;
          this.uploadProgress = null;
        });
    } else {
      // Send message without files
      this.sendMessageToChat(chatId, message, []);
    }
  }

  /**
   * Send message to chat (with or without file attachments)
   */
  private sendMessageToChat(chatId: string, message: string, fileUrls: string[]): void {
    // Clear UI
    this.newMessage = '';
    this.previewFiles = [];
    this.uploadProgress = null;
    var containsMedia = false;

    // Build message content with file URLs
    let finalMessage = message.trim();
    
    // Append file URLs to message (they will be displayed in UI)
    if (fileUrls.length > 0) {
      containsMedia = true;
      const fileUrlsText = fileUrls.join('\n');
      finalMessage = finalMessage ? `${finalMessage}\n${fileUrlsText}` : fileUrlsText;
      console.log('📎 Attaching files to message:', fileUrls);
    }

    if (!finalMessage) {
      return;
    }

    console.log('📤 Sending message to chat:', finalMessage);

    // Call API based on chat type
    if (this.selectedChatType === 'direct') {
      this.chatService.sendDirectMessage(chatId, finalMessage, containsMedia)
        .pipe(takeUntil(this.destroy$))
        .subscribe({
          next: (response: any) => {
            console.log('✅ Message sent successfully:', response);
          },
          error: (err: any) => {
            console.error('❌ Error sending message:', err);
            this.newMessage = message;
            this.previewFiles = []; // Files already uploaded, don't retry
          }
        });
    } else {
      this.chatService.sendGroupMessage(chatId, finalMessage, containsMedia)
        .pipe(takeUntil(this.destroy$))
        .subscribe({
          next: (response: any) => {
            console.log('✅ Group message sent successfully:', response);
          },
          error: (err: any) => {
            console.error('❌ Error sending group message:', err);
            this.newMessage = message;
            this.previewFiles = []; // Files already uploaded, don't retry
          }
        });
    }
  }

  private subscribeToWebSocketMessages(): void {
    console.log('🔔 Subscribing to WebSocket messages');
    this.webSocketService.message$
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: (message) => {
          console.log('📨 Message received via WebSocket:', message);
          // Check if message is for current chat
          if (this.selectedChat && message?.message) {
            // Check if message already exists in array (to avoid duplicates)
            const isDuplicate = this.messages.some(msg => 
              msg.publicId === message.publicId ||
              (msg.sender?.publicId === message.sender?.publicId && 
               msg.createdAt === message.createdAt && 
               msg.message === message.message)
            );
            
            if (!isDuplicate) {
              // Add received message to the list
              this.messages.push(message);
              
              // Preload presigned URLs for files in this message
              const files = this.getMessageFiles(message);
              if (files.length > 0) {
                this.preloadPresignedUrls(files);
              }
              
              console.log('✅ Message added to chat');
            } else {
              console.log('⚠️ Duplicate message received, skipping');
            }
          }
        },
        error: (err) => {
          console.error('❌ WebSocket error:', err);
        }
      });
  }

  private resolveEntityId(entity: any): string | null {
    if (!entity) return null;

    const candidate = entity.publicId ?? entity.userPublicId ?? entity.userId ?? entity.id;
    if (candidate === undefined || candidate === null) return null;

    const normalized = String(candidate).trim();
    return normalized.length > 0 ? normalized : null;
  }

  private isSameUser(userA: any, userB: any): boolean {
    const idA = this.resolveEntityId(userA);
    const idB = this.resolveEntityId(userB);
    return !!idA && !!idB && idA === idB;
  }

  private getOtherParticipant(chat: DirectChat): any | null {
    if (!chat?.user1 || !chat?.user2) return null;
    if (!this.currentUserId) return chat.user1;

    const currentUser = { publicId: this.currentUserId };
    if (this.isSameUser(chat.user1, currentUser)) return chat.user2;
    if (this.isSameUser(chat.user2, currentUser)) return chat.user1;

    // Fallback when current user relation cannot be resolved from payload
    return chat.user1;
  }

  isCurrentUserMessage(message: any): boolean {
    if (!this.currentUserId) return false;
    const senderId = this.resolveEntityId(message?.sender);
    return !!senderId && senderId === this.currentUserId;
  }

  getChatName(chat: DirectChat | GroupChat): string {
    // Check if it's a group chat
    if ('groupName' in chat) {
      return chat.groupName || 'Group Chat';
    }
    
    // It's a direct message chat
    const otherUser = this.getOtherParticipant(chat);
    if (!otherUser) return 'Chat';
    return otherUser.username || otherUser.email || 'Unknown User';
  }

  getOtherUserName(chat: DirectChat): string {
    if (!chat.user1 || !chat.user2) return 'Chat';
    
    // If current user is user1, show user2's name, otherwise show user1's name
    if (this.currentUserId === chat.user1.publicId) {
      return chat.user2.username || chat.user2.email || 'Unknown User';
    } else {
      return chat.user1.username || chat.user1.email || 'Unknown User';
    }
  }

  getOtherUserId(chat: DirectChat | GroupChat): string | null {
    if (!this.isDirectChat(chat)) return null;
    
    const directChat = chat as DirectChat;
    return this.resolveEntityId(this.getOtherParticipant(directChat));
  }

  isUserOnline(userId: string | null): boolean {
    if (!userId) return false;
    return this.presenceService.isUserOnline(userId);
  }

  isDirectChat(chat: DirectChat | GroupChat): boolean {
    return 'user1' in chat;
  }

  isGroupChat(chat: DirectChat | GroupChat): boolean {
    return 'groupName' in chat;
  }

  openCreateGroupModal(): void {
    this.showCreateGroupModal = true;
    this.createGroupForm = {
      groupName: '',
      description: '',
      selectedMembers: []
    };
    this.memberSearchQuery = '';
    this.memberSearchResults = [];
    
    // Load all users for selection
    if (this.allUsers.length === 0) {
      this.loadAllUsers();
    }
  }

  closeCreateGroupModal(): void {
    this.showCreateGroupModal = false;
    this.createGroupForm = {
      groupName: '',
      description: '',
      selectedMembers: []
    };
    this.memberSearchQuery = '';
    this.memberSearchResults = [];
  }

  toggleGroupDetails(): void {
    this.showGroupDetails = !this.showGroupDetails;
  }

  private loadAllUsers(): void {
    // This would call a UserService to get all users
    // For now, we'll load them from people/users endpoint if available
    this.chatService.getAllUsers()
      .pipe(takeUntil(this.destroy$))
      .subscribe(
        (users: any) => {
          // Filter out current user
          this.allUsers = (users || []).filter((u: any) => u.publicId !== this.currentUserId);
          console.log('✅ All users loaded:', this.allUsers.length);
        },
        (error: any) => {
          console.error('❌ Error loading users:', error);
          this.allUsers = [];
        }
      );
  }

  searchMembers(): void {
    if (!this.memberSearchQuery.trim()) {
      this.memberSearchResults = [];
      return;
    }

    const query = this.memberSearchQuery.toLowerCase();
    this.memberSearchResults = this.allUsers.filter(user => 
      (user.username && user.username.toLowerCase().includes(query)) ||
      (user.email && user.email.toLowerCase().includes(query))
    );
  }

  isUserSelected(user: any): boolean {
    return this.createGroupForm.selectedMembers.some(m => m.publicId === user.publicId);
  }

  toggleMemberSelection(user: any): void {
    const index = this.createGroupForm.selectedMembers.findIndex(m => m.publicId === user.publicId);
    if (index >= 0) {
      this.createGroupForm.selectedMembers.splice(index, 1);
    } else {
      this.createGroupForm.selectedMembers.push(user);
    }
  }

  submitCreateGroup(): void {
    if (!this.createGroupForm.groupName.trim()) {
      console.warn('⚠️ Group name is required');
      return;
    }

    console.log('📝 Creating group:', this.createGroupForm.groupName);
    
    const request = {
      groupName: this.createGroupForm.groupName,
      description: this.createGroupForm.description || '',
      memberPublicIds: this.createGroupForm.selectedMembers.map(m => m.publicId)
    };

    this.chatService.createGroupChat(request)
      .pipe(takeUntil(this.destroy$))
      .subscribe(
        (newGroup: any) => {
          console.log('✅ Group created:', newGroup);
          this.groupChats.push(newGroup);
          this.updateChatList();
          this.closeCreateGroupModal();
          // Auto-select the new group
          if (this.chatType === 'all' || this.chatType === 'group') {
            this.selectChat(newGroup);
          }
        },
        (error: any) => {
          console.error('❌ Error creating group:', error);
          alert('Failed to create group. Please try again.');
        }
      );
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }
}

