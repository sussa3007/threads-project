package com.challenge.project.api.controller;

import com.challenge.project.file.service.AwsService;
import com.challenge.project.http.service.InstagramLoginService;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.apache.catalina.loader.ResourceEntry;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
@RequestMapping("/api")
public class BestTh8ApiController {

    private final AwsService awsService;

    private final InstagramLoginService loginService;

    @DeleteMapping("/image")
    public ResponseEntity<?> delete() {

        return ResponseEntity.ok().build();

    }

    @GetMapping("/instagram/login")
    public ResponseEntity getLogin(
            @RequestParam("u") String username,
            @RequestParam("p") String password

    ) throws Exception {
        return ResponseEntity.ok(loginService.requestLogin(username, password));
    }
}
