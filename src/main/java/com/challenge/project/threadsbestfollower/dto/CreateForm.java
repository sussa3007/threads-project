package com.challenge.project.threadsbestfollower.dto;

import lombok.Getter;
import lombok.Setter;
import jakarta.validation.constraints.NotEmpty;

@Getter
@Setter
public class CreateForm {

    @NotEmpty(message = "아이디 입력해")
    private String username;
}
