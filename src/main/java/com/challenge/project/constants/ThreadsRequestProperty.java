package com.challenge.project.constants;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ThreadsRequestProperty {
    INSTAGRAM_API_APP_ID("936619743392459"),
    BASE_URL("https://www.threads.net/api/graphql"),
    INSTA_URL("https://www.instagram.com/"),
    USER_ID_GET_URL_RAPID("https://threads-by-instagram-fast.p.rapidapi.com/users/id?username="),
    USER_ID_GET_INSTAGRAM_URL("https://i.instagram.com/api/v1/users/web_profile_info/?username="),
    DEFAULT_LSD_TOKEN("_5qPwLctQlmS2s9NVNA80s"),
    X_IG_APP_ID("238260118697367");

    private final String id;

}
