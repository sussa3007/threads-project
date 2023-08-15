package com.challenge.project.http.service;

import com.challenge.project.http.dto.FollowerResponseDto;
import com.challenge.project.http.dto.InstagramTokenDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class ThreadsFollowerServiceTest {

    @Autowired
    private ThreadsFollowerService service;

    @Test
    @DisplayName("")
    void test() {
        //Given

        String username = "";
        String password = "";
        try {
            List<FollowerResponseDto> follower = service.findAllFollower(username, password);
            System.out.println(follower.size());
        } catch (Exception e) {
            e.printStackTrace();
        }
        //When

        //Then
    }

}