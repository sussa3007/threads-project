package com.challenge.project.http.service;


import com.challenge.project.constants.DocId;
import com.challenge.project.constants.ErrorCode;
import com.challenge.project.constants.ThreadsRequestProperty;
import com.challenge.project.exception.ServiceLogicException;
import com.challenge.project.http.dto.*;
import com.challenge.project.http.handler.HttpResponseHandler;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.exc.UnrecognizedPropertyException;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.Consts;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
@Slf4j
public class ThreadsRankingService {

    private final HttpResponseHandler handler;

    private final HeaderService headerService;

    private final InstagramLoginService loginService;

    @Value("${RAPID_API_KEY}")
    private String RAPID_API_KEY;

    @Value("${INSTAGRAM_COOKIE}")
    private String INSTAGRAM_COOKIE;

    private final Gson gson = new Gson();

    public List<String> getThreadsIdList(String username) throws IOException, InterruptedException {
        try (CloseableHttpClient httpclient = HttpClients.createDefault()) {
            String userId = findUserIdByUserName(username);
            try {
                log.info("Call Get Threads Id List(Default)");
                String token = getLsdToken();
                HttpPost httpPost = new HttpPost(ThreadsRequestProperty.BASE_URL.getProperty());
                headerService.setThreadsRequestDefaultHeader(httpPost, token, null);
                UrlEncodedFormEntity entity = getUrlEncodedBody("userID", userId, token, DocId.GET_PROFILE_POST.getId());
                httpPost.setEntity(entity);
                log.info("Executing request = {} ", httpPost.getRequestLine());
                HttpClientPostDto execute = (HttpClientPostDto) httpclient.execute(httpPost, handler.getThreadsRankingResponseHandler());
                return parsingThreadsIdList(execute);
            } catch (Exception e) {
                log.error("Default Threads Id Parser Error");
                log.info("Call Get Threads Id List(Rapid)");
                return getThreadsIdListByRapidApi(userId);
            }
        } catch (UnrecognizedPropertyException un) {
            throw new ServiceLogicException(ErrorCode.NOT_FOUND);
        } catch (Exception e) {
            throw e;
        }
    }

    public List<String> getThreadsIdListByRapidApi(String userId) throws IOException {
        try (CloseableHttpClient httpclient = HttpClients.createDefault()) {
            HttpGet httpGet = new HttpGet(ThreadsRequestProperty.THREADS_GET_URL_RAPID.getProperty()+userId);
            headerService.setRapidApiDefaultHeader(httpGet, RAPID_API_KEY);
            log.info("Executing request ThreadsId RapidApi = {} ", httpGet.getRequestLine());
            String  execute = (String) httpclient.execute(httpGet, handler.getDefaultStringHandler());
            return parsingThreadsIdListByRapidApi(execute);
        } catch (UnrecognizedPropertyException un) {
            throw new ServiceLogicException(ErrorCode.NOT_FOUND);
        } catch (Exception e) {
            throw e;
        }
    }



    public List<FindUserDto> getReplyUser(List<String> postList, String username) throws IOException, InterruptedException {
        try (CloseableHttpClient httpclient = HttpClients.createDefault()) {
            String token = getLsdToken();
            List<FindUserDto> userList = new ArrayList<>();
            // 병렬 스트림 처리
            postList.stream().parallel().forEach( postId -> {
                log.info("Call Post Reply User List = {}", postId);
                HttpPost httpPost = new HttpPost(ThreadsRequestProperty.BASE_URL.getProperty());
                headerService.setThreadsRequestDefaultHeader(httpPost, token, null);
                UrlEncodedFormEntity entity = getUrlEncodedBody("postID", postId, token, DocId.GET_POST.getId());
                httpPost.setEntity(entity);
                HttpClientPostDto execute = null;
                try {
                    execute = (HttpClientPostDto) httpclient.execute(httpPost, handler.getThreadsRankingResponseHandler());
                } catch (IOException e) {
                    log.error("Error Not Found Post = {}", e.getMessage());
                    throw new ServiceLogicException(ErrorCode.INTERNAL_SERVER_ERROR);
                }
                log.info("Executing request = {} ", httpPost.getRequestLine());
                addUserAndParsingReplyUser(execute, username, userList);
            });
            return userList;

        } catch (Exception e) {
            throw e;
        }

    }

    private String findUserIdByUserName(String username) throws IOException, InterruptedException {
        String userId = getUserIdByInstagramApi(username);
        if (userId == null) {
            String userIdByRapidApi = Optional.ofNullable(getUserIdByRapidApi(username))
                    .orElseGet(() -> UserDto.builder().userId(null).build())
                    .getUserId();
            if (userIdByRapidApi == null) {
                String userIdCrawling = getUserIdCrawling(username);
                log.info("Init UserId userIdCrawling");
                userId = userIdCrawling;
            } else {
                log.info("Init UserId userIdByRapidApi");
                userId = userIdByRapidApi;
            }
            if (userId == null) throw new ServiceLogicException(ErrorCode.NOT_FOUND);
        } else {
            log.info("Init UserId userIdByInstagramApi");
        }
        return userId;
    }

    private void addUserAndParsingReplyUser(
            HttpClientPostDto execute,
            String username,
            List<FindUserDto> userList
    ) {
        JsonElement el = JsonParser.parseString(gson.toJson(execute.getData()));
        JsonArray array = el.getAsJsonObject().get("data")
                .getAsJsonObject().get("reply_threads")
                .getAsJsonArray();
        log.info(">>> thread_items Parsing Reply User");
        for (JsonElement element : array) {
            for (JsonElement jsonElement : element.getAsJsonObject().get("thread_items").getAsJsonArray()) {
                String un = jsonElement.getAsJsonObject().get("post").getAsJsonObject().get("user").getAsJsonObject().get("username").getAsString();
                String url = jsonElement.getAsJsonObject().get("post").getAsJsonObject().get("user").getAsJsonObject().get("profile_pic_url").getAsString();
                if (!un.equals(username)) {
                    FindUserDto findUser = FindUserDto.of(un, url);

                    userList.add(findUser);
                }
            }

        }
    }


    private UrlEncodedFormEntity getUrlEncodedBody(String varKey, String userId, String token, String doc_id) {
        List<NameValuePair> form = new ArrayList<>();
        form.add(new BasicNameValuePair("variables", "{\"" + varKey + "\": \"" + userId + "\"}"));
        form.add(new BasicNameValuePair("doc_id", doc_id));
        form.add(new BasicNameValuePair("lsd", token));
        return new UrlEncodedFormEntity(form, Consts.UTF_8);
    }

    private String getLsdToken() throws IOException, InterruptedException {
        try {
            HttpClient clientFwd = HttpClient.newHttpClient();
            HttpRequest get = HttpRequest.newBuilder()
                    .uri(URI.create(ThreadsRequestProperty.BASE_URL.getProperty()))
                    //GET, POST, PUT,..
                    .method("GET", HttpRequest.BodyPublishers.noBody())
                    .header("user-agent", "threads-client")
                    .header("x-ig-app-id", ThreadsRequestProperty.X_IG_APP_ID.getProperty())
                    .header("content-type", "application/x-www-form-urlencoded")
                    .header("x-fb-lsd", ThreadsRequestProperty.DEFAULT_LSD_TOKEN.getProperty())
                    .build();
            HttpResponse<String> send = clientFwd.send(get, HttpResponse.BodyHandlers.ofString());
            int findIndex = send.body().indexOf("\"LSD\",[],{\"token\"");
            int startIndex = findIndex + 19;
            int endIndex = startIndex + 22;
            return send.body().substring(startIndex, endIndex);
        } catch (Exception e) {
            log.error("Error LSD Token Parsing = {}", e.getMessage());
            log.error("Return Default LSD Token");
            return ThreadsRequestProperty.DEFAULT_LSD_TOKEN.getProperty();
        }

    }

    private String getUserIdCrawling(String username) throws IOException, InterruptedException {
        try {
            log.info("Call getUserIdCrawling");
            String pageContents = "";
            StringBuilder contents = new StringBuilder();

            URL url = new URL(ThreadsRequestProperty.INSTA_URL.getProperty()+username);
            URLConnection con = (URLConnection)url.openConnection();
            InputStreamReader reader = new InputStreamReader(con.getInputStream(), StandardCharsets.UTF_8);

            BufferedReader buff = new BufferedReader(reader);

            while((pageContents = buff.readLine())!=null){
                contents.append(pageContents);
                contents.append("\r\n");
            }
            buff.close();
            String body = contents.toString();
            String regex = "\"user_id\":\"(\\d+)\"";
            Pattern pattern = Pattern.compile(regex);
            Matcher matcher = pattern.matcher(body);
            if (matcher.find()) {
                log.info("GroupCount Called Crawling = {}",matcher.groupCount());
                log.info("UserID Called Crawling = {}",matcher.group(1));
                return matcher.group(1);
            } else {
                log.error("### ERROR Not Found getUserIdCrawling ###");
                return null;
            }
        } catch (Exception e) {
            log.error("Error = {}", e.getMessage());
            return null;
        }
    }


    private UserDto getUserIdByRapidApi(String username) throws IOException {
        try (CloseableHttpClient httpclient = HttpClients.createDefault()) {
            log.info("Call getUserIdByRapidApi");
            HttpGet httpGet = new HttpGet(ThreadsRequestProperty.USER_ID_GET_URL_RAPID.getProperty() + username);
            headerService.setRapidApiDefaultHeader(httpGet, RAPID_API_KEY);
            HttpClientUsersDto execute = (HttpClientUsersDto) httpclient.execute(httpGet, handler.getThreadsRankingResponseHandler());
            log.info("Executing request RapidAPI = {} ", httpGet.getRequestLine());
            log.info("UserID Called RapidAPI = {}",execute.getUserId());
            return UserDto.of(execute);
        } catch (ServiceLogicException se) {
            log.error("Error = {}", se.getErrorCode().getMessage());
            return null;
        } catch (Exception e) {
            log.error("Error = {}", e.getMessage());
            return null;
        }
    }
    private String getUserIdByInstagramApi(String username) throws IOException {
        try (CloseableHttpClient httpclient = HttpClients.createDefault()) {
            log.info("Call getUserIdByInstagramApi");
            HttpGet httpGet = new HttpGet(ThreadsRequestProperty.USER_ID_GET_INSTAGRAM_URL.getProperty() + username);
            headerService.setUserIdByInstagramApiHeader(httpGet, INSTAGRAM_COOKIE);
            InstagramResponseDto execute = (InstagramResponseDto) httpclient.execute(httpGet, handler.getThreadsRankingResponseHandler());
            log.info("Executing request InstagramAPI = {} ", httpGet.getRequestLine());
            if (execute.getStatus().equals("ok")) {
                JsonElement jsonElement = JsonParser.parseString(gson.toJson(execute.getData()));
                String userId = jsonElement.getAsJsonObject().get("user").getAsJsonObject().get("id").getAsString();
                log.info("UserID Called InstagramApi = {}",userId);
                return userId;
            } else {
                throw new ServiceLogicException(ErrorCode.NOT_FOUND);
            }
        } catch (ServiceLogicException se) {
            log.error("Error = {}", se.getErrorCode().getMessage());
            return null;
        } catch (Exception e) {
            log.error("Error = {}", e.getMessage());
            return null;
        }
    }

    private List<String> parsingThreadsIdList(HttpClientPostDto execute) {
        JsonElement el = JsonParser.parseString(gson.toJson(execute.getData()));
        JsonArray array = el.getAsJsonObject().get("mediaData").getAsJsonObject().get("threads").getAsJsonArray();
        List<String> idList = new ArrayList<>();
        for (JsonElement id : array) {
            String findId = String.valueOf(id.getAsJsonObject().get("id")).replaceAll("\"", "");
            idList.add(findId);
        }
        return idList;
    }

    private List<String> parsingThreadsIdListByRapidApi(String  execute) {
        JsonElement el = JsonParser.parseString(execute);
        JsonArray array = el.getAsJsonObject().get("threads").getAsJsonArray();
        List<String> idList = new ArrayList<>();
        for (JsonElement id : array) {
            String findId = id.getAsJsonObject().get("node").getAsJsonObject().get("id").getAsString().replaceAll("\"", "");
            idList.add(findId);
        }
        return idList;
    }


}
