package com.challenge.project.http.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.Map;

@AllArgsConstructor
@Builder
@Data
public class ParamsDto {

    Map<String, String > client_input_params;

    Map<String, String > server_params;
}
