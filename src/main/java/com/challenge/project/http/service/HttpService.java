package com.challenge.project.http.service;


import com.challenge.project.constants.DocId;
import com.challenge.project.constants.ErrorCode;
import com.challenge.project.constants.ThreadsRequestProperty;
import com.challenge.project.exception.ServiceLogicException;
import com.challenge.project.http.dto.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.exc.UnrecognizedPropertyException;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.Consts;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
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
public class HttpService {

    private final String BASE_URL = ThreadsRequestProperty.BASE_URL.getId();
    private final String INSTA_URL = ThreadsRequestProperty.INSTA_URL.getId();

    private final String USER_ID_GET_URL_RAPID = ThreadsRequestProperty.USER_ID_GET_URL_RAPID.getId();
    private final String USER_ID_GET_INSTAGRAM_URL = ThreadsRequestProperty.USER_ID_GET_INSTAGRAM_URL.getId();

    // /"LSD",[],{"token":"([^"]+)"}/

    @Value("${RAPID_API_KEY}")
    private String RAPID_API_KEY;

    @Value("${INSTAGRAM_COOKIE}")
    private String INSTAGRAM_COOKIE;

    private final ObjectMapper mapper = new ObjectMapper();

    private final Gson gson = new Gson();

    public List<String> getThreadsIdList(String username) throws IOException, InterruptedException {
        try (CloseableHttpClient httpclient = HttpClients.createDefault()) {
            String userId = findUserIdByUserName(username);
            String token = getLsdToken();
            HttpPost httpPost = new HttpPost(BASE_URL);
            httpPost.setHeader("user-agent", "threads-client");
            httpPost.setHeader("x-ig-app-id", ThreadsRequestProperty.X_IG_APP_ID.getId());
            httpPost.setHeader("content-type", "application/x-www-form-urlencoded");
            httpPost.setHeader("x-fb-lsd", token);
            UrlEncodedFormEntity entity = getUrlEncodedBody("userID", userId, token, DocId.GET_PROFILE_POST.getId());
            httpPost.setEntity(entity);
            HttpClientPostDto execute = (HttpClientPostDto) httpclient.execute(httpPost, getResponseHandler());
            log.info("Executing request = {} ", httpPost.getRequestLine());
            return parsingThreadsIdList(execute);

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
                HttpPost httpPost = new HttpPost(BASE_URL);
                httpPost.setHeader("user-agent", "threads-client");
                httpPost.setHeader("x-ig-app-id", ThreadsRequestProperty.X_IG_APP_ID.getId());
                httpPost.setHeader("content-type", "application/x-www-form-urlencoded");
                httpPost.setHeader("x-fb-lsd", token);
                UrlEncodedFormEntity entity = getUrlEncodedBody("postID", postId, token, DocId.GET_POST.getId());
                httpPost.setEntity(entity);
                HttpClientPostDto execute = null;
                try {
                    execute = (HttpClientPostDto) httpclient.execute(httpPost, getResponseHandler());
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
                    .uri(URI.create(BASE_URL))
                    //GET, POST, PUT,..
                    .method("GET", HttpRequest.BodyPublishers.noBody())
                    .header("user-agent", "threads-client")
                    .header("x-ig-app-id", ThreadsRequestProperty.X_IG_APP_ID.getId())
                    .header("content-type", "application/x-www-form-urlencoded")
                    .header("x-fb-lsd", ThreadsRequestProperty.DEFAULT_LSD_TOKEN.getId())
                    .build();
            HttpResponse<String> send = clientFwd.send(get, HttpResponse.BodyHandlers.ofString());
            int findIndex = send.body().indexOf("\"LSD\",[],{\"token\"");
            int startIndex = findIndex + 19;
            int endIndex = startIndex + 22;
            return send.body().substring(startIndex, endIndex);
        } catch (Exception e) {
            log.error("Error LSD Token Parsing = {}", e.getMessage());
            log.error("Return Default LSD Token");
            return ThreadsRequestProperty.DEFAULT_LSD_TOKEN.getId();
        }

    }

    private String getUserIdCrawling(String username) throws IOException, InterruptedException {
        try {
            log.info("Call getUserIdCrawling");
            String pageContents = "";
            StringBuilder contents = new StringBuilder();

            URL url = new URL(INSTA_URL+username);
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
            HttpGet httpGet = new HttpGet(USER_ID_GET_URL_RAPID + username);
            httpGet.setHeader("X-RapidAPI-Host", "threads-by-instagram-fast.p.rapidapi.com");
            httpGet.setHeader("X-RapidAPI-Key", RAPID_API_KEY);
            HttpClientUsersDto execute = (HttpClientUsersDto) httpclient.execute(httpGet, getResponseHandler());
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
            HttpGet httpGet = new HttpGet(USER_ID_GET_INSTAGRAM_URL + username);
            httpGet.setHeader("x-ig-app-id", ThreadsRequestProperty.INSTAGRAM_API_APP_ID.getId());
            httpGet.setHeader("Cookie", INSTAGRAM_COOKIE);
            InstagramResponseDto execute = (InstagramResponseDto) httpclient.execute(httpGet, getResponseHandler());
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



    private ResponseHandler<?> getResponseHandler() {
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
