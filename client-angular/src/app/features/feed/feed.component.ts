import { Component, OnInit, OnDestroy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { MatIconModule } from '@angular/material/icon';
import { LoaderComponent } from '../../shared/components/loader.component';
import { FeedService, Post, PostComment } from '../../core/services/feed.service';
import { AuthService } from '../../core/services/auth.service';
import { Subject } from 'rxjs';
import { takeUntil } from 'rxjs/operators';

@Component({
  selector: 'app-feed',
  standalone: true,
  imports: [CommonModule, FormsModule, LoaderComponent, MatIconModule],
  templateUrl: './feed.component.html',
  styleUrls: ['./feed.component.scss']
})
export class FeedComponent implements OnInit, OnDestroy {
  posts: Post[] = [];
  newPost = '';
  loadingPosts = false;
  creatingPost = false;
  currentUserId: string | null = null;
  expandedPostId: string | null = null;
  postComments: { [postId: string]: PostComment[] } = {};
  newComments: { [postId: string]: string } = {};
  loadingComments: { [postId: string]: boolean } = {};
  private destroy$ = new Subject<void>();

  constructor(
    private feedService: FeedService,
    private authService: AuthService
  ) {
    this.authService.currentUser$
      .pipe(takeUntil(this.destroy$))
      .subscribe(user => {
        this.currentUserId = user?.publicId || null;
      });
  }

  ngOnInit(): void {
    this.loadPosts();
  }

  refreshPosts(): void {
    this.loadPosts();
  }

  private loadPosts(): void {
    this.loadingPosts = true;
    this.feedService.getAllPosts(0, 20)
      .pipe(takeUntil(this.destroy$))
      .subscribe(
        (response: any) => {
          let postList = Array.isArray(response) ? response : (response?.content || response?.data || []);
          this.posts = postList;
          console.log('✅ Posts loaded:', this.posts.length);
          this.loadingPosts = false;
        },
        error => {
          console.error('❌ Error loading posts:', error);
          this.loadingPosts = false;
        }
      );
  }

  createPost(): void {
    if (!this.newPost.trim()) return;

    this.creatingPost = true;
    this.feedService.createPost(this.newPost)
      .pipe(takeUntil(this.destroy$))
      .subscribe(
        (post: Post) => {
          console.log('✅ Post created:', post);
          this.posts.unshift(post);
          this.newPost = '';
          this.creatingPost = false;
        },
        error => {
          console.error('❌ Error creating post:', error);
          this.creatingPost = false;
        }
      );
  }

  deletePost(postId: string): void {
    if (!confirm('Are you sure you want to delete this post?')) return;

    this.feedService.deletePost(postId)
      .pipe(takeUntil(this.destroy$))
      .subscribe(
        () => {
          this.posts = this.posts.filter(p => p.publicId !== postId);
          console.log('✅ Post deleted');
        },
        error => console.error('❌ Error deleting post:', error)
      );
  }

  toggleLike(post: Post): void {
    if (post.likedByCurrentUser) {
      this.feedService.unlikePost(post.publicId)
        .pipe(takeUntil(this.destroy$))
        .subscribe(
          (response: any) => {
            post.likedByCurrentUser = false;
            post.likeCount = response.likeCount || 0;
          },
          error => console.error('❌ Error unliking post:', error)
        );
    } else {
      this.feedService.likePost(post.publicId)
        .pipe(takeUntil(this.destroy$))
        .subscribe(
          (response: any) => {
            post.likedByCurrentUser = true;
            post.likeCount = response.likeCount || 0;
          },
          error => console.error('❌ Error liking post:', error)
        );
    }
  }

  toggleComments(postId: string): void {
    if (this.expandedPostId === postId) {
      this.expandedPostId = null;
    } else {
      this.expandedPostId = postId;
      if (!this.postComments[postId]) {
        this.loadComments(postId);
      }
    }
  }

  private loadComments(postId: string): void {
    this.loadingComments[postId] = true;
    this.feedService.getPostComments(postId, 0, 10)
      .pipe(takeUntil(this.destroy$))
      .subscribe(
        (response: any) => {
          let commentList = Array.isArray(response) ? response : (response?.content || response?.data || []);
          this.postComments[postId] = commentList;
          this.loadingComments[postId] = false;
          console.log('✅ Comments loaded:', commentList.length);
        },
        error => {
          console.error('❌ Error loading comments:', error);
          this.loadingComments[postId] = false;
        }
      );
  }

  createPostComment(postId: string): void {
    const content = this.newComments[postId]?.trim();
    if (!content) return;

    this.feedService.createPostComment(postId, content)
      .pipe(takeUntil(this.destroy$))
      .subscribe(
        (postComment: PostComment) => {
          if (!this.postComments[postId]) {
            this.postComments[postId] = [];
          }
          this.postComments[postId].push(postComment);
          this.newComments[postId] = '';
          
          // Update post comment count
          const post = this.posts.find(p => p.publicId === postId);
          if (post) {
            post.commentCount++;
          }
          console.log('✅ Post comment created');
        },
        error => console.error('❌ Error creating post comment:', error)
      );
  }

  deletePostComment(postCommentId: string): void {
    if (!confirm('Are you sure you want to delete this post comment?')) return;

    this.feedService.deletePostComment(postCommentId)
      .pipe(takeUntil(this.destroy$))
      .subscribe(
        () => {
          for (const postId in this.postComments) {
            this.postComments[postId] = this.postComments[postId].filter(c => c.publicId !== postCommentId);
          }
          console.log('✅ Post comment deleted');
        },
        error => console.error('❌ Error deleting post comment:', error)
      );
  }

  togglePostCommentLike(postComment: PostComment, postId: string): void {
    if (postComment.likedByCurrentUser) {
      this.feedService.unlikePostComment(postComment.publicId)
        .pipe(takeUntil(this.destroy$))
        .subscribe(
          (response: any) => {
            postComment.likedByCurrentUser = false;
            postComment.likeCount = response.likeCount || 0;
          },
          error => console.error('❌ Error unliking post comment:', error)
        );
    } else {
      this.feedService.likePostComment(postComment.publicId)
        .pipe(takeUntil(this.destroy$))
        .subscribe(
          (response: any) => {
            postComment.likedByCurrentUser = true;
            postComment.likeCount = response.likeCount || 0;
          },
          error => console.error('❌ Error liking post comment:', error)
        );
    }
  }

  isPostAuthor(post: Post): boolean {
    return post.author.publicId === this.currentUserId;
  }

  isPostCommentAuthor(postComment: PostComment): boolean {
    return postComment.author.publicId === this.currentUserId;
  }

  getCommentText(postId: string): string {
    return (this.newComments[postId] || '').trim();
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }
}
