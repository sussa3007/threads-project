package com.challenge.project.http.service;

import com.challenge.project.http.dto.InstagramTokenDto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class InstagramLoginServiceTest {

    @Autowired
    InstagramLoginService service;

    @Test
    void test() throws Exception {
        InstagramTokenDto tokenDto = service.requestLogin("", "");
        System.out.println(tokenDto.getToken());
        System.out.println(tokenDto.getUserId());
    }

}