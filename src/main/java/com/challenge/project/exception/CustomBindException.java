package com.challenge.project.exception;

import com.challenge.project.constants.ErrorCode;
import lombok.Getter;
import org.springframework.boot.context.properties.bind.BindResult;
import org.springframework.validation.BindingResult;

public class CustomBindException extends RuntimeException{

    @Getter
    private BindingResult bindResult;

    public CustomBindException(BindingResult bindResult) {
        this.bindResult = bindResult;
    }
}
