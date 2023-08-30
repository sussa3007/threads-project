package com.challenge.project.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ResponseDto<T> {

    private Result result;

    private T data;

    public static <T> ResponseDto<T> of(T data, Result result) {
        ResponseDto<T> response = new ResponseDto<>();
        response.result = result;
        response.data = data;
        return response;
    }

    public static <T> ResponseDto<T> of(Result result) {
        ResponseDto<T> response = new ResponseDto<>();
        response.result = result;
        response.data = null;
        return response;
    }
}