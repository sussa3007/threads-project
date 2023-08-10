package com.challenge.project.http.dto;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class ThreadsFollowersResponseDto {

    private List<FollowerResponseDto> users;

    @JsonProperty("big_list")
    private boolean bigList;

    @JsonProperty("page_size")
    private Long pageSize;

    @JsonProperty("next_max_id")
    private String nextMaxId;

    @JsonProperty("has_more")
    private Boolean hasMore;

    @JsonProperty("groups")
    private List<Object> groups;

    @JsonProperty("status")
    private String status;


}
