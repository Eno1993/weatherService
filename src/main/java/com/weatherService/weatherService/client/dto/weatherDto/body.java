package com.weatherService.weatherService.client.dto.weatherDto;

import lombok.Data;

@Data
public class body {
    private String dataType;
    private items items;
    private int pageNo;
    private int numOfRows;
    private int totalCount;
}
