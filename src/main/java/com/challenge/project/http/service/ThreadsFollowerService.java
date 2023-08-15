package com.challenge.project.http.service;

import com.challenge.project.constants.ThreadsRequestProperty;
import com.challenge.project.http.dto.FollowerResponseDto;
import com.challenge.project.http.dto.InstagramTokenDto;
import com.challenge.project.http.dto.ThreadsFollowersResponseDto;
import com.challenge.project.http.handler.HttpResponseHandler;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ThreadsFollowerService {

    private final InstagramLoginService loginService;

    private final HeaderService headerService;

    private final HttpResponseHandler handler;

    private final ObjectMapper mapper;

    private final String test = "";


    // Todo 임시로 토큰 반환
    public List<FollowerResponseDto> findAllFollower(String username, String password) throws Exception {
        InstagramTokenDto tokenDto = loginService.requestLogin(username, password);
        return findFollower(username, password, tokenDto);
    }

    private List<FollowerResponseDto> findFollower(
            String username,
            String password,
            InstagramTokenDto tokenDto
    ) throws Exception {
        List<FollowerResponseDto> list = new ArrayList<>();
        try (CloseableHttpClient httpclient = HttpClients.createDefault()) {
            log.info("Call Request Threads Followers");
            HttpGet httpGet;

            if (tokenDto.getMaxId() == null) {
                httpGet = new HttpGet(ThreadsRequestProperty.createFollowerRequestUri(tokenDto.getUserId()));
            } else {
                httpGet = new HttpGet(ThreadsRequestProperty.createFollowerNextPageRequestUri(tokenDto.getUserId(), tokenDto.getMaxId()));
            }
            headerService.setThreadsFollowerRequestHeader(httpGet, tokenDto.getToken());
            log.info("Executing request InstagramAPI = {} ", httpGet.getRequestLine());
            String execute = (String) httpclient.execute(httpGet, handler.getThreadsFollowerHandler());
            ThreadsFollowersResponseDto threadsFollowersResponseDto = mapper.readValue(execute, ThreadsFollowersResponseDto.class);
            if (execute.contains("next_max_id")) {
                log.info("Followers Request Status = {}",threadsFollowersResponseDto.getStatus());
                tokenDto.setMaxId(threadsFollowersResponseDto.getNextMaxId());
                list.addAll(threadsFollowersResponseDto.getUsers());
                log.info("Followers Request Size = {}",threadsFollowersResponseDto.getUsers().size());
                list.addAll(findFollower(username, password, tokenDto));
            } else {
                log.info("Last Followers Request Status = {}",threadsFollowersResponseDto.getStatus());
                list.addAll(threadsFollowersResponseDto.getUsers());
                log.info("Last Followers Request Size = {}",list.size());
            }
            return list;

        } catch (Exception e) {
            log.error("Error = {}", e.getMessage());
            throw e;
        }
    }
}
