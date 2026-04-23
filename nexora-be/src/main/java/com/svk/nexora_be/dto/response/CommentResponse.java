package com.svk.nexora_be.dto.response;

import com.svk.nexora_be.entity.Comment;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CommentResponse {
    private String publicId;
    private String content;
    private UserResponse author;
    private long likeCount;
    private boolean likedByCurrentUser;
    private long createdAt; // timestamp in milliseconds
    private long updatedAt;

    public static CommentResponse fromComment(Comment comment, long likeCount, boolean likedByCurrentUser) {
        return CommentResponse.builder()
                .publicId(comment.getPublicId())
                .content(comment.getContent())
                .author(UserResponse.fromUser(comment.getAuthor()))
                .likeCount(likeCount)
                .likedByCurrentUser(likedByCurrentUser)
                .createdAt(java.sql.Timestamp.valueOf(comment.getCreatedAt()).getTime())
                .updatedAt(java.sql.Timestamp.valueOf(comment.getUpdatedAt()).getTime())
                .build();
    }
}
