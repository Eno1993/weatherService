package com.weatherService.weatherService.client.dto.weatherDto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class response {
    @JsonProperty("header")
    private header header;
    @JsonProperty("body")
    private body body;
}
