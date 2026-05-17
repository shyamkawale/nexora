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

export interface PostComment {
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

  // Post Comments
  createPostComment(postId: string, content: string): Observable<PostComment> {
    console.log('💬 Creating post comment on post:', postId);
    return this.httpService.post<PostComment>('/api/v1/post-comments', {
      postId,
      content
    });
  }

  getPostComments(postId: string, page: number = 0, size: number = 10): Observable<any> {
    console.log('💬 Fetching post comments for post:', postId);
    return this.httpService.get(`/api/v1/post-comments/post/${postId}?page=${page}&size=${size}`);
  }

  deletePostComment(postCommentId: string): Observable<any> {
    console.log('🗑️ Deleting post comment:', postCommentId);
    return this.httpService.delete(`/api/v1/post-comments/${postCommentId}`);
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

  likePostComment(postCommentId: string): Observable<any> {
    console.log('❤️ Liking post comment:', postCommentId);
    return this.httpService.post(`/api/v1/likes/post-comments/${postCommentId}`, {});
  }

  unlikePostComment(postCommentId: string): Observable<any> {
    console.log('🤍 Unliking post comment:', postCommentId);
    return this.httpService.delete(`/api/v1/likes/post-comments/${postCommentId}`);
  }

  getPostCommentLikeCount(postCommentId: string): Observable<any> {
    console.log('❤️ Getting like count for post comment:', postCommentId);
    return this.httpService.get(`/api/v1/likes/post-comments/${postCommentId}/count`);
  }
}
