package com.challenge.project.http.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BodyDto {
    private String variables;

    private String doc_id;

    private String lsd;
}
