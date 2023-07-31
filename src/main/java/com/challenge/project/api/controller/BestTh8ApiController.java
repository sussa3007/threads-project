package com.challenge.project.api.controller;

import com.challenge.project.file.service.AwsService;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.apache.catalina.loader.ResourceEntry;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
@RequestMapping("/api")
public class BestTh8ApiController {

    private final AwsService awsService;

    @DeleteMapping("/image")
    public ResponseEntity<?> delete() {

        return ResponseEntity.ok().build();

    }


}
