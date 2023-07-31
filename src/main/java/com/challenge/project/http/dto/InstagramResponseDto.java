package com.challenge.project.http.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class InstagramResponseDto {
    Object data;

    Object status;

    InstagramResponseDto(Object data, Object status) {
        this.data = data;
        this.status = status;
    }

    public static InstagramResponseDto of(Object data, Object status) {
        return new InstagramResponseDto(data, status);
    }
}
