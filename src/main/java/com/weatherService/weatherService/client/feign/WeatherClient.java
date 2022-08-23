package com.weatherService.weatherService.client.feign;

import com.weatherService.weatherService.client.dto.weatherDto.vilageFcst;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "weather-client", url = "http://apis.data.go.kr/1360000/VilageFcstInfoService_2.0")
public interface WeatherClient {

    @RequestMapping(
            method = RequestMethod.GET,
            value = "/getVilageFcst",
            produces = "application/json"
    )
    vilageFcst getWeatherInfo(
            @RequestParam("serviceKey") String serviceKey,
            @RequestParam("pageNo") String pageNo,
            @RequestParam("numOfRows") String numOfRows,
            @RequestParam("dataType") String dataType,
            @RequestParam("base_date") String baseDate,
            @RequestParam("base_time") String baseTime,
            @RequestParam("nx") String nx,
            @RequestParam("ny") String ny
    );
}
