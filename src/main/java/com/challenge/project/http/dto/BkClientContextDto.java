package com.challenge.project.http.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.Map;

@AllArgsConstructor
@Builder
@Data
public class BkClientContextDto {
    String bloks_version;
    String styles_id;
}
