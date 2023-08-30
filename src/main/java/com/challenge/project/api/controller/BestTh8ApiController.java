package com.challenge.project.api.controller;

import com.challenge.project.dto.RankingResponseDto;
import com.challenge.project.dto.ResponseDto;
import com.challenge.project.dto.Result;
import com.challenge.project.file.service.AwsService;
import com.challenge.project.file.service.FileService;
import com.challenge.project.generatedata.entity.GenerateData;
import com.challenge.project.generatedata.service.GenerateDataService;
import com.challenge.project.http.service.InstagramLoginService;
import com.challenge.project.threadsbestfollower.dto.CreateViewResponseDto;
import com.challenge.project.threadsbestfollower.dto.UserResponseDto;
import com.challenge.project.threadsbestfollower.service.ThreadsBestFollowerService;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.apache.catalina.loader.ResourceEntry;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/api")
public class BestTh8ApiController {

    private final AwsService awsService;

    private final InstagramLoginService loginService;

    private final ThreadsBestFollowerService service;

    private final FileService fileService;

    private final GenerateDataService dataService;

    @GetMapping("/instagram/login")
    public ResponseEntity getLogin(
            @RequestParam("u") String username,
            @RequestParam("p") String password

    ) throws Exception {
        return ResponseEntity.ok(loginService.requestLogin(username, password));
    }

    @GetMapping("/ranking")
    public ResponseEntity<?> getRanking(
            @RequestParam("username") String username
    ) {
        List<UserResponseDto> list = fileService
                .imageLocalDownloader(
                        service.getRankingFollower(
                                username.toLowerCase().replaceAll(" ", "")
                        )
                );
        Long count = dataService.getCountingData();
        String info = service.setUserTagGap(list);
        LocalDateTime generateData = dataService.createGenerateData(username, info);
        RankingResponseDto response = RankingResponseDto.builder()
                .user(list)
                .userTag(info)
                .counting(count)
                .createAt(generateData)
                .build();

        return ResponseEntity.ok(ResponseDto.of(response, Result.ok()));
    }
}
