package com.challenge.project.threadsbestfollower.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class CreateViewResponseDto {

    private List<UserResponseDto> data;

    private String info;

    private Long counting;

}
