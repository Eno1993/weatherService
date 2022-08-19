package com.weatherService.weatherService.domian;

import com.weatherService.weatherService.domian.en.WeatherStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class Weather {

    private WeatherStatus weatherStatus;
    private int cloudCover;//적운량
    private int tmp;
    private String fcstDate;
    private String fcstTime;

    public void setCloudCover(int cloudCover) {
        this.cloudCover = cloudCover;
        if(0<=this.cloudCover&&this.cloudCover<=5){
            this.weatherStatus = WeatherStatus.맑음;
        }else if(this.cloudCover<=8){
            this.weatherStatus = WeatherStatus.구름많음;
        }else{
            this.weatherStatus = WeatherStatus.흐림;
        }
    }

    public void setTmp(int tmp){
        this.tmp = tmp;
    }

    public void setFcstDate(String fcstDate){
        this.fcstDate = fcstDate;
    }

    public void setFcstTime(String fcstTime){
        this.fcstTime = fcstTime;
    }

}
