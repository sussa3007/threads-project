package com.challenge.project.http.service;

import com.challenge.project.http.dto.InstagramTokenDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ThreadsFollowerService {

    private final InstagramLoginService loginService;

    public List<String> findFollower(String username, String password) {
        InstagramTokenDto tokenDto = loginService.requestLogin(username, password);

        return null;
    }
}
