package com.challenge.project.http.service;

import com.challenge.project.constants.ErrorCode;
import com.challenge.project.constants.ThreadsRequestProperty;
import com.challenge.project.exception.ServiceLogicException;
import com.challenge.project.http.dto.BkClientContextDto;
import com.challenge.project.http.dto.InstagramTokenDto;
import com.challenge.project.http.dto.ParamsDto;
import com.challenge.project.http.handler.HttpResponseHandler;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.*;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class InstagramLoginService {

    private final InstagramPasswordEncodingService passwordEncodingService;

    private final HttpResponseHandler handler;

    private final HeaderService headerService;

    private final ObjectMapper mapper = new ObjectMapper();

    private final Gson gson = new Gson();

    public InstagramTokenDto requestLogin(String username, String password) {
        try (CloseableHttpClient httpclient = HttpClients.createDefault()) {
            String encryptPassword = passwordEncodingService.getEncryptPassword(password);
            UrlEncodedFormEntity entity = makeRequestLoginPostEntity(encryptPassword, username);
            log.info("Call Request Instagram Login");
            HttpPost httpPost = new HttpPost(ThreadsRequestProperty.INSTAGRAM_LOGIN_REQUEST_URL.getProperty());
            headerService.setInstagramLoginApiDefaultHeader(httpPost);
            log.info("Executing request InstagramAPI = {} ", httpPost.getRequestLine());
            httpPost.setEntity(entity);
            return getToken((String) httpclient.execute(httpPost, handler.getInstagramApiResponseHandler()));
        } catch (Exception e) {
            log.error("Error = {}", e.getMessage());
            return null;
        }
    }

    private InstagramTokenDto getToken(String responseBody) {
        if (responseBody.contains("IG-Set-Authorization")) {

            JsonElement jsonElement = JsonParser.parseString(responseBody);
            String getString = jsonElement.getAsJsonObject().get("layout")
                    .getAsJsonObject().get("bloks_payload")
                    .getAsJsonObject().get("tree")
                    .getAsJsonObject().get("㐟")
                    .getAsJsonObject().get("#")
                    .getAsString().replaceAll("\\\\", "");
            String headerBody = getString.substring(getString.indexOf("\"headers\":\"") + 1, getString.lastIndexOf("}\""));

            int start = headerBody.indexOf("\"{") + 1;
            int end = headerBody.indexOf("}\"") + 1;
            String authBody = headerBody.substring(start, end)
                    .replaceAll("\"\\{", "{") + "}";
            String userId = getUserId(headerBody);
            String token = JsonParser.parseString(authBody)
                    .getAsJsonObject()
                    .get("IG-Set-Authorization")
                    .getAsString();

            return InstagramTokenDto.builder()
                    .userId(userId)
                    .token(token)
                    .build();
        } else {
            throw new ServiceLogicException(ErrorCode.BAD_REQUEST);
        }
    }

    private String getUserId(String headerBody) {
        if (headerBody.contains("ig-set-ig-u-ds-user-id")) {
            String subStrPk = headerBody.substring(headerBody.indexOf("ig-set-ig-u-ds-user-id"));
            int pkStart = subStrPk.indexOf(" ");
            int pkEnd = subStrPk.indexOf(",");
            return subStrPk.substring(pkStart, pkEnd).replaceAll(" ", "");
        } else {
            throw new ServiceLogicException(ErrorCode.NOT_FOUND);
        }
    }


    private UrlEncodedFormEntity makeRequestLoginPostEntity(String encryptPassword, String username) {
        Map<String, String> clientInputParams = Map.of(
                "password", encryptPassword,
                "contact_point", username,
                "device_id", "android-" + passwordEncodingService.generateAndroidId()
        );
        Map<String, String> serverParams = Map.of(
                "credential_type", "password",
                "device_id", "android-" + passwordEncodingService.generateAndroidId()
        );
        ParamsDto paramsDto = ParamsDto.builder()
                .client_input_params(clientInputParams)
                .server_params(serverParams)
                .build();
        BkClientContextDto bkClientContextDto = BkClientContextDto.builder()
                .bloks_version(ThreadsRequestProperty.BLOKS_VERSION_KEY.getProperty())
                .styles_id("instagram")
                .build();
        return getUrlEncodedBodyForRequestLogin(paramsDto, bkClientContextDto);
    }

    private UrlEncodedFormEntity getUrlEncodedBodyForRequestLogin(ParamsDto paramsDto, BkClientContextDto bkClientContextDto) {
        List<NameValuePair> form = new ArrayList<>();
        form.add(new BasicNameValuePair("params", gson.toJson(paramsDto)));
        form.add(new BasicNameValuePair("bk_client_context", gson.toJson(bkClientContextDto)));
        form.add(new BasicNameValuePair("bloks_versioning_id", bkClientContextDto.getBloks_version()));
        return new UrlEncodedFormEntity(form, Consts.UTF_8);
    }

}
