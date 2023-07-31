package com.challenge.project.http.dto;

import com.google.gson.JsonElement;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FindUserDto {

    private String username;

    private String profileUrl;

    public static FindUserDto of (String username, String profileUrl) {
        return FindUserDto.builder()
                .username(username)
                .profileUrl(profileUrl)
                .build()
        ;

    }

}
