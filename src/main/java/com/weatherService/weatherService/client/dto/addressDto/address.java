package com.weatherService.weatherService.client.dto.addressDto;

import lombok.Data;

@Data
public class address {

    private String zipcode;
    private String category;
    private String road;
    private String parcel;
    private String bldnm;
    private String bldnmdc;
}
