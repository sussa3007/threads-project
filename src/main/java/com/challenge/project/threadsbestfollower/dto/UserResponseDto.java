package com.challenge.project.threadsbestfollower.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserResponseDto {
    private String username;

    private String profileUrl;

    private Long replyCount;

    private int rankingCount;

    public static UserResponseDto of (String username, String profileUrl, Long replyCount) {
        return UserResponseDto.builder()
                .username(username)
                .profileUrl(profileUrl)
                .replyCount(replyCount)
                .build()
                ;
    }

}
