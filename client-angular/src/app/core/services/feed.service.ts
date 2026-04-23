import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { HttpService } from './http.service';

export interface Post {
  publicId: string;
  content: string;
  author: any;
  likeCount: number;
  commentCount: number;
  likedByCurrentUser: boolean;
  createdAt: number;
  updatedAt: number;
}

export interface Comment {
  publicId: string;
  content: string;
  author: any;
  likeCount: number;
  likedByCurrentUser: boolean;
  createdAt: number;
  updatedAt: number;
}

@Injectable({
  providedIn: 'root'
})
export class FeedService {
  constructor(private httpService: HttpService) {}

  // Posts
  createPost(content: string): Observable<Post> {
    console.log('📝 Creating post');
    return this.httpService.post<Post>('/api/v1/posts', { content });
  }

  getAllPosts(page: number = 0, size: number = 20): Observable<any> {
    console.log('📋 Fetching all posts');
    return this.httpService.get(`/api/v1/posts?page=${page}&size=${size}`);
  }

  getPost(postId: string): Observable<Post> {
    console.log('📄 Fetching post:', postId);
    return this.httpService.get<Post>(`/api/v1/posts/${postId}`);
  }

  getUserPosts(userPublicId: string): Observable<Post[]> {
    console.log('👤 Fetching user posts:', userPublicId);
    return this.httpService.get<Post[]>(`/api/v1/posts/user/${userPublicId}`);
  }

  deletePost(postId: string): Observable<any> {
    console.log('🗑️ Deleting post:', postId);
    return this.httpService.delete(`/api/v1/posts/${postId}`);
  }

  // Comments
  createComment(postId: string, content: string): Observable<Comment> {
    console.log('💬 Creating comment on post:', postId);
    return this.httpService.post<Comment>('/api/v1/comments', {
      postId,
      content
    });
  }

  getPostComments(postId: string, page: number = 0, size: number = 10): Observable<any> {
    console.log('💬 Fetching comments for post:', postId);
    return this.httpService.get(`/api/v1/comments/post/${postId}?page=${page}&size=${size}`);
  }

  deleteComment(commentId: string): Observable<any> {
    console.log('🗑️ Deleting comment:', commentId);
    return this.httpService.delete(`/api/v1/comments/${commentId}`);
  }

  // Likes
  likePost(postId: string): Observable<any> {
    console.log('❤️ Liking post:', postId);
    return this.httpService.post(`/api/v1/likes/posts/${postId}`, {});
  }

  unlikePost(postId: string): Observable<any> {
    console.log('🤍 Unliking post:', postId);
    return this.httpService.delete(`/api/v1/likes/posts/${postId}`);
  }

  getPostLikeCount(postId: string): Observable<any> {
    console.log('❤️ Getting like count for post:', postId);
    return this.httpService.get(`/api/v1/likes/posts/${postId}/count`);
  }

  likeComment(commentId: string): Observable<any> {
    console.log('❤️ Liking comment:', commentId);
    return this.httpService.post(`/api/v1/likes/comments/${commentId}`, {});
  }

  unlikeComment(commentId: string): Observable<any> {
    console.log('🤍 Unliking comment:', commentId);
    return this.httpService.delete(`/api/v1/likes/comments/${commentId}`);
  }

  getCommentLikeCount(commentId: string): Observable<any> {
    console.log('❤️ Getting like count for comment:', commentId);
    return this.httpService.get(`/api/v1/likes/comments/${commentId}/count`);
  }
}
