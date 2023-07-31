package com.challenge.project.http.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class HttpClientPostDto {

    Object data;

    Object extensions;


    HttpClientPostDto(Object data, Object extensions) {
        this.data = data;
        this.extensions = extensions;
    }

    public static HttpClientPostDto of(Object data, Object extensions) {
        return new HttpClientPostDto(data, extensions);
    }
}
