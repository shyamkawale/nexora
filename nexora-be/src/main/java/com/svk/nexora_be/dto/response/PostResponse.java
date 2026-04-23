package com.svk.nexora_be.dto.response;

import com.svk.nexora_be.entity.Post;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PostResponse {
    private String publicId;
    private String content;
    private UserResponse author;
    private long likeCount;
    private long commentCount;
    private boolean likedByCurrentUser;
    private long createdAt; // timestamp in milliseconds
    private long updatedAt;

    public static PostResponse fromPost(Post post, long likeCount, long commentCount, boolean likedByCurrentUser) {
        return PostResponse.builder()
                .publicId(post.getPublicId())
                .content(post.getContent())
                .author(UserResponse.fromUser(post.getAuthor()))
                .likeCount(likeCount)
                .commentCount(commentCount)
                .likedByCurrentUser(likedByCurrentUser)
                .createdAt(java.sql.Timestamp.valueOf(post.getCreatedAt()).getTime())
                .updatedAt(java.sql.Timestamp.valueOf(post.getUpdatedAt()).getTime())
                .build();
    }
}
