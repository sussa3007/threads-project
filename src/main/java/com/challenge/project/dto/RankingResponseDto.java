package com.challenge.project.dto;

import com.challenge.project.threadsbestfollower.dto.UserResponseDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class RankingResponseDto {

    private List<UserResponseDto> user;

    private String userTag;

    private Long counting;

    private LocalDateTime createAt;

}
