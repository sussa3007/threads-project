package com.challenge.project.http.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class HttpClientUsersDto {

    Object userId;

    Object profile;
    Object username;



    HttpClientUsersDto(
            Object userId,
            Object profile,
            Object username
    ) {
        this.userId = userId;
        this.profile = profile;
        this.username = username;
    }

    public static HttpClientUsersDto of(
            Object userId,
            Object profile,
            Object username
    ) {
        return new HttpClientUsersDto(
                userId,
                profile,
                username
        );
    }
}