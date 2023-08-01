package com.challenge.project.constants;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum DocId {
    GET_PROFILE_POST("6232751443445612"),
    GET_POST("5587632691339264"),
    GET_PROFILE_REPLIES("6307072669391286"),
    GET_LIKED_POST("9360915773983802");

    private final String id;

}
