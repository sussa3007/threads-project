package com.challenge.project.http.service;

import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
class HttpServiceTest {

    @Mock
    private MockMvc mvc;

    @Autowired
    private HttpService service;



}