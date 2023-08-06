package com.challenge.project.http.handler;

import com.challenge.project.constants.ErrorCode;
import com.challenge.project.exception.ServiceLogicException;
import com.challenge.project.http.dto.HttpClientPostDto;
import com.challenge.project.http.dto.HttpClientUsersDto;
import com.challenge.project.http.dto.InstagramResponseDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpEntity;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.util.EntityUtils;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class HttpResponseHandler {

    private final ObjectMapper mapper = new ObjectMapper();

    public ResponseHandler<?> getInstagramApiResponseHandler() {
        return response -> {
            int status = response.getStatusLine().getStatusCode();
            if (status >= 200 && status < 300) {
                HttpEntity responseBody = response.getEntity();
                //Todo : response를 한번 이상 파싱하면 예외 발생
                String res = EntityUtils.toString(responseBody);
                if (res.contains("㐟")) {
                    log.info("Return = {}", "response body");
                    return res;
                } else {
                    log.info("Return = {}", "response");
                    return response;
                }
            } else {
                if (status == 404) {
                    throw new ServiceLogicException(ErrorCode.NOT_FOUND);
                } else {
                    throw new ClientProtocolException("Unexpected response status: " + status);

                }
            }
        };
    }

    public ResponseHandler<?> getThreadsRankingResponseHandler() {
        return response -> {
            int status = response.getStatusLine().getStatusCode();
            if (status >= 200 && status < 300) {
                HttpEntity responseBody = response.getEntity();
                //Todo : response를 한번 이상 파싱하면 예외 발생
                String res = EntityUtils.toString(responseBody);
                if (res.contains("userId")) {
                    log.info("Return = {}", "HttpClientUserDto");
                    return mapper.readValue(
                            res,
                            HttpClientUsersDto.class);
                } else if (res.contains("extensions")) {
                    log.info("Return = {}", "HttpClientPostDto");
                    return mapper.readValue(
                            res,
                            HttpClientPostDto.class
                    );
                } else {
                    log.info("Return = {}", "InstagramResponseDto");
                    return mapper.readValue(
                            res,
                            InstagramResponseDto.class
                    );
                }

            } else {
                if (status == 404) {
                    throw new ServiceLogicException(ErrorCode.NOT_FOUND);
                } else {
                    throw new ClientProtocolException("Unexpected response status: " + status);

                }
            }
        };
    }



}

