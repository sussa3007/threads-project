package com.challenge.project.advice;


import com.challenge.project.constants.ErrorCode;
import com.challenge.project.exception.ServiceLogicException;
import com.challenge.project.exception.dto.ErrorResponse;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@Slf4j
@RestControllerAdvice(annotations = {RestController.class})
public class ApiControllerExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ResponseEntity<Object> exceptionHandler(
            Exception e,
            WebRequest request
    ) {
        e.printStackTrace();
        return handleExceptionInternal(e, ErrorCode.INTERNAL_SERVER_ERROR, request);
    }

    @ExceptionHandler
    public ResponseEntity<Object> serviceLogicExceptionHandler(
            ServiceLogicException e,
            WebRequest request
    ) {
        if (e.getErrorCode().getStatusCode() == 500) {
            e.printStackTrace();
        }
        return handleExceptionInternal(e, e.getErrorCode(), request);
    }

    private ResponseEntity<Object> handleExceptionInternal(Exception e, ErrorCode errorCode, WebRequest request) {
        return handleExceptionInternal(e, errorCode, HttpHeaders.EMPTY, HttpStatus.valueOf(errorCode.getStatusCode()), request);
    }

    @Override
    protected ResponseEntity<Object> handleExceptionInternal(
            Exception ex, Object body, HttpHeaders headers, HttpStatusCode statusCode, WebRequest request
    ) {
        return handleExceptionInternal(ex, ErrorCode.valueOf(statusCode.toString()), headers, statusCode, request);
    }

    private ResponseEntity<Object> handleExceptionInternal(
            Exception e, ErrorCode errorCode, HttpHeaders headers, HttpStatus status, WebRequest request) {
        return super.handleExceptionInternal(
                e,
                ErrorResponse.of(errorCode),
                headers,
                status,
                request
        );
    }
}
