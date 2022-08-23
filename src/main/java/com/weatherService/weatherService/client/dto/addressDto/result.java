package com.weatherService.weatherService.client.dto.addressDto;

import lombok.Data;

import java.util.List;

@Data
public class result {

    private String crs;
    private String type;
    private List<item> items;
}
