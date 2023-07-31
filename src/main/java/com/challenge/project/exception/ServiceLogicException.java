package com.challenge.project.exception;

import com.challenge.project.constants.ErrorCode;
import lombok.Getter;

@Getter
public class ServiceLogicException extends RuntimeException {

    private ErrorCode errorCode;

    public ServiceLogicException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }
}