package com.weatherService.weatherService.client.dto.addressDto;

import lombok.Data;

@Data
public class response {

    private service service;
    private String status;
    private record record;
    private page page;
    private result result;
}
