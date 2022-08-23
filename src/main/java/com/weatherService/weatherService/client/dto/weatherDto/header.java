package com.weatherService.weatherService.client.dto.weatherDto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
public class header {
    private String resultCode;
    private String resultMsg;
}
