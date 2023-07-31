package com.challenge.project.http.dto;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {

    private String userId;

    private String username;

    private String profile;



    public static UserDto of(HttpClientUsersDto dto) {
        return UserDto.builder()
                .userId((String) dto.getUserId())
                .username((String) dto.getUsername())
                .profile((String) dto.getProfile())
                .build();
    }

}
