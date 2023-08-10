package com.challenge.project.http.service;


import com.challenge.project.constants.ThreadsRequestProperty;
import com.google.gson.Gson;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpHeaders;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class HeaderService {


    private final Gson gson = new Gson();

    public void setInstagramLoginApiDefaultHeader(HttpPost httpPost) {
        httpPost.setHeader(HttpHeaders.USER_AGENT, "Barcelona 289.0.0.77.109 Android");
        httpPost.setHeader("Sec-Fetch-Site", "same-origin");
        httpPost.setHeader(HttpHeaders.CONTENT_TYPE, "application/x-www-form-urlencoded; charset=UTF-8");
    }

    public void setThreadsRequestDefaultHeader(HttpPost httpPost, String token) {
        httpPost.setHeader(HttpHeaders.USER_AGENT, "threads-client");
        httpPost.setHeader("x-ig-app-id", ThreadsRequestProperty.X_IG_APP_ID.getProperty());
        httpPost.setHeader(HttpHeaders.CONTENT_TYPE, "application/x-www-form-urlencoded");
        httpPost.setHeader("x-fb-lsd", token);
    }

    public void setRapidApiDefaultHeader(HttpGet httpGet, String apiKey) {
        httpGet.setHeader("X-RapidAPI-Host", "threads-by-instagram-fast.p.rapidapi.com");
        httpGet.setHeader("X-RapidAPI-Key", apiKey);
    }

    public void setThreadsFollowerRequestHeader(HttpGet httpGet, String authToken) {
        httpGet.setHeader("Sec-Fetch-Site", "same-origin");
        httpGet.setHeader(HttpHeaders.AUTHORIZATION, authToken);
        httpGet.setHeader(HttpHeaders.CONTENT_TYPE, "application/x-www-form-urlencoded; charset=UTF-8");
        httpGet.setHeader(HttpHeaders.USER_AGENT, "Barcelona 289.0.0.77.109 Android");
    }

    public void setUserIdByInstagramApiHeader(HttpGet httpGet, String cookie) {
        httpGet.setHeader("x-ig-app-id", ThreadsRequestProperty.INSTAGRAM_API_APP_ID.getProperty());
        httpGet.setHeader("Cookie", cookie);
    }


}
