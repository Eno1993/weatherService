package com.weatherService.weatherService.client.feign;

import com.weatherService.weatherService.client.dto.addressDto.search;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.persistence.PreUpdate;


@FeignClient(name = "address-client", url = "http://api.vworld.kr/req")
public interface AddressClient {

    @RequestMapping(
            method = RequestMethod.GET,
            value = "/search",
            produces = "application/json"
    )
    search getAddressInfo(
            @RequestParam("service") String service,
            @RequestParam("request") String request,
            @RequestParam("version") String version,
            @RequestParam("crs") String crs,
            @RequestParam("size") String size,
            @RequestParam("page") String page,
            @RequestParam("query") String query,
            @RequestParam("type") String type,
            @RequestParam("category") String category,
            @RequestParam("format") String format,
            @RequestParam("errorformat") String errorformat,
            @RequestParam("key") String key
            );
}
