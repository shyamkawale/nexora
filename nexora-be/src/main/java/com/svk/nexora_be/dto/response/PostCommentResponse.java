package com.svk.nexora_be.dto.response;

import com.svk.nexora_be.entity.PostComment;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PostCommentResponse {
    private String publicId;
    private String content;
    private UserResponse author;
    private long likeCount;
    private boolean likedByCurrentUser;
    private long createdAt; // timestamp in milliseconds
    private long updatedAt;

    public static PostCommentResponse fromPostComment(PostComment postComment, long likeCount, boolean likedByCurrentUser) {
        return PostCommentResponse.builder()
                .publicId(postComment.getPublicId())
                .content(postComment.getContent())
                .author(UserResponse.fromUser(postComment.getAuthor()))
                .likeCount(likeCount)
                .likedByCurrentUser(likedByCurrentUser)
                .createdAt(java.sql.Timestamp.valueOf(postComment.getCreatedAt()).getTime())
                .updatedAt(java.sql.Timestamp.valueOf(postComment.getUpdatedAt()).getTime())
                .build();
    }
}
