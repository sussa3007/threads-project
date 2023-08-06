package com.challenge.project.advice;


import com.challenge.project.threadsbestfollower.dto.CreateForm;
import com.challenge.project.constants.ErrorCode;
import com.challenge.project.exception.CustomBindException;
import com.challenge.project.exception.ServiceLogicException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;

import java.util.Map;

@ControllerAdvice
@Slf4j
public class BaseExceptionHandler {

    @ExceptionHandler
    public ModelAndView general(
            ServiceLogicException e,
            HttpServletRequest request) {
        ErrorCode errorCode = e.getErrorCode();
        if (e.getErrorCode().equals(ErrorCode.NOT_FOUND)) {
            return new ModelAndView(
                    "alert",
                    Map.of(
                            "msg", "아이디 다시 입력해봐❗❗",
                            "nextPage", "/",
                            "backUrl", "/"
                    )
            );
        } else {
            return new ModelAndView(
                    "alert",
                    Map.of(
                            "msg", "지금 너무 많아😭 다시해봐❗❗",
                            "nextPage", "/",
                            "backUrl", "/"
                    )
            );
        }

    }

    @ExceptionHandler
    public ModelAndView velidateBindingException(
            CustomBindException e,
            HttpServletRequest request,
            HttpServletResponse response
    ) {
        log.error("### Binding Error");

        return new ModelAndView(
                "index",
                Map.of(
                        "createForm", new CreateForm(),
                        "hasError", true,
                        "errorMessage", e.getBindResult().getFieldError().getDefaultMessage(),
                        "countMessage","비공개 프로필 외않되? 하지마"
                        )
        );
    }

}

