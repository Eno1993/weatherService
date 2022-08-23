package com.weatherService.weatherService.service;

import com.weatherService.weatherService.client.dto.weatherDto.vilageFcst;
import com.weatherService.weatherService.domian.Address;
import com.weatherService.weatherService.domian.SessionInfo;
import com.weatherService.weatherService.domian.UserInfo;
import com.weatherService.weatherService.domian.Weather;
import com.weatherService.weatherService.repository.AddressCrud;
import com.weatherService.weatherService.repository.SessionCrud;
import com.weatherService.weatherService.repository.UserCrud;
import com.weatherService.weatherService.client.feign.WeatherClient;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.*;

@Service
@Slf4j
public class WeatherService {

    @Autowired
    private UserCrud userCrud;
    @Autowired
    private AddressCrud addressCrud;
    @Autowired
    private SessionCrud sessionCrud;
    @Autowired
    private WeatherClient weatherClient;
    private final String key = "1bPCbBtr1ndsGEZ3S9F/mX9Zv9SBNPveHu3GIGaWkpocDn3jw7rkU2GCCxeMsSVLkh/BL+R/Nup+X1LstbrMcA==";
    private Map<String, List<String[]>> map = new HashMap<>();

    public Weather getWeather(String sessionStr){

        SessionInfo session = sessionCrud.getSessionBySessionStr(sessionStr);
        if(session==null){
            throw new IllegalStateException("session 이 만료되었거나 존재하지 않습니다.");
        }

        UserInfo user = userCrud.getUserById(session.getUserId());
        if(user==null){
            throw new IllegalStateException("사용자 정보를 받아오지 못했습니다.");
        }

        Address address = addressCrud.getAddressById(user.getAddressId());
        if(address==null){
            throw new IllegalStateException("사용자 주소 정보를 받아오지 못했습니다.");
        }

        LocalDateTime time = LocalDateTime.now();
        if(time.getHour()<2){
            time.minusDays(1);
        }
        Date date = Timestamp.valueOf(time);

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
        String baseDate = dateFormat.format(date);
        String baseTime = getBaseTime(time.getHour(), time.getMinute());

        time.plusHours(1);
        date = Timestamp.valueOf(time);
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH00");
        String hour = timeFormat.format(date);

        GpsTransfer gpsTransfer = new GpsTransfer();
        gpsTransfer.transfer(1, address.getX(), address.getY());
        System.out.println(gpsTransfer.getLat()+"  "+gpsTransfer.getLng());

        int x = (int)Math.round(gpsTransfer.getLat());
        int y = (int)Math.round(gpsTransfer.getLng());

        StringBuffer buffer = new StringBuffer();
        String strResult = "";
        try{
            StringBuilder urlBuilder = new StringBuilder("http://apis.data.go.kr/1360000/VilageFcstInfoService_2.0/getVilageFcst");
            urlBuilder.append("?" + URLEncoder.encode("serviceKey","UTF-8") + "=" + URLEncoder.encode(key, "UTF-8"));
            urlBuilder.append("&" + URLEncoder.encode("pageNo","UTF-8") + "=" + URLEncoder.encode("1", "UTF-8"));
            urlBuilder.append("&" + URLEncoder.encode("numOfRows","UTF-8") + "=" + URLEncoder.encode("1000", "UTF-8"));
            urlBuilder.append("&" + URLEncoder.encode("dataType","UTF-8") + "=" + URLEncoder.encode("JSON", "UTF-8"));
            urlBuilder.append("&" + URLEncoder.encode("base_date","UTF-8") + "=" + URLEncoder.encode(baseDate, "UTF-8"));
            urlBuilder.append("&" + URLEncoder.encode("base_time","UTF-8") + "=" + URLEncoder.encode(baseTime, "UTF-8"));
            urlBuilder.append("&" + URLEncoder.encode("nx","UTF-8") + "="+ URLEncoder.encode(String.valueOf(x), "UTF-8"));
            urlBuilder.append("&" + URLEncoder.encode("ny","UTF-8") + "=" + URLEncoder.encode(String.valueOf(y), "UTF-8"));
            URL url = new URL(urlBuilder.toString());
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            conn.setRequestMethod("GET");
            conn.setRequestProperty("Content-Type","application/json");

            BufferedReader rd;
            if(200<=conn.getResponseCode()&&conn.getResponseCode()<300){
                rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            }else{
                rd = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
            }

            String line;
            while(true){
                line = rd.readLine();
                if(line==null){ break; }
                buffer.append(line);
            }
            rd.close();
            conn.disconnect();
            strResult = buffer.toString();

        }catch (Exception e){
            throw new IllegalStateException("서버 연결 오류.");
        }

        parsingJsonToMap(strResult);

        if(map.containsKey(baseDate+hour)){
            List<String[]> list = map.get(baseDate+hour);
            Weather weather = new Weather();
            for(int i=0; i<list.size(); i++){
                if(list.get(i)[0].equals("SKY")){ weather.setCloudCover(Integer.parseInt(list.get(i)[1])); }
                if(list.get(i)[0].equals("TMP")){ weather.setTmp(Integer.parseInt(list.get(i)[1])); }
            }
            weather.setFcstDate(baseDate);
            weather.setFcstTime(hour);
            return weather;
        }else{
            throw new IllegalStateException("날씨 정보를 제대로 받아오지 못했습니다.");
        }
    }

    public void parsingJsonToMap(String strResult) {

        JSONObject jsonObject = new JSONObject(strResult);
        JSONObject response = jsonObject.getJSONObject("response");
        JSONObject header = response.getJSONObject("header");
        String resultCode = header.getString("resultCode");
        checkResultCode(resultCode);
        JSONObject body = response.getJSONObject("body");
        JSONObject items = body.getJSONObject("items");
        JSONArray item = items.getJSONArray("item");
        for(int i=0; i<item.length()&&i<14; i++){
            JSONObject nowItem = item.getJSONObject(i);

            String fcstDate = nowItem.getString("fcstDate");
            String fcstTime = nowItem.getString("fcstTime");
            String category = nowItem.getString("category");
            String fcstValue = nowItem.getString("fcstValue");

            String mapKey = fcstDate+fcstTime;
            String[] strArr = new String[]{category, fcstValue};

            if(map.containsKey(mapKey)){
                map.get(mapKey).add(strArr);
            }else{
                List<String[]> list = new ArrayList<>();
                list.add(strArr);
                map.put(mapKey, list);
            }
        }
    }

    private void checkResultCode(String resultCode) {
        if(resultCode.equals("00")){
            return;
        }else if(resultCode.equals("01")){
            throw new IllegalStateException("어플리케이션 에러.");
        }else if(resultCode.equals("02")){
            throw new IllegalStateException("데이터베이스 에러.");
        }else if(resultCode.equals("03")){
            throw new IllegalStateException("데이터없음 에러.");
        }else{
            throw new IllegalStateException("기타 에러 사항.");
        }
    }

    public String getBaseTime(int hour, int minute){
        if(23<=hour){
            if(10<minute){ return "2300"; }
            else{ return "2000"; }
        }
        if(20<=hour){
            if(10<minute){ return "2000"; }
            else{ return "1700"; }
        }
        if(17<=hour){
            if(10<minute){ return "1700"; }
            else{ return "1400"; }
        }
        if(14<=hour){
            if(10<minute){ return "1400"; }
            else{ return "1100"; }
        }
        if(11<=hour){
            if(10<minute){ return "1100"; }
            else{ return "0800"; }
        }
        if(8<=hour){
            if(10<minute){ return "0800"; }
            else{ return "0500"; }
        }
        if(5<=hour){
            if(10<minute){ return "0500"; }
            else{ return "0200"; }
        }
        if(2<=hour){
            if(10<minute){ return "0200"; }
            else{ return "2300"; }
        }
        return "2300";
    }

    public vilageFcst getWeatherInfo(String sessionStr, int pageNo, int numOfRows){
        SessionInfo session = sessionCrud.getSessionBySessionStr(sessionStr);
        if(session==null){
            throw new IllegalStateException("session 이 만료되었거나 존재하지 않습니다.");
        }

        UserInfo user = userCrud.getUserById(session.getUserId());
        if(user==null){
            throw new IllegalStateException("사용자 정보를 받아오지 못했습니다.");
        }

        Address address = addressCrud.getAddressById(user.getAddressId());
        if(address==null){
            throw new IllegalStateException("사용자 주소 정보를 받아오지 못했습니다.");
        }

        LocalDateTime time = LocalDateTime.now();
        if(time.getHour()<2){
            time.minusDays(1);
        }
        Date date = Timestamp.valueOf(time);

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
        String baseDate = dateFormat.format(date);
        String baseTime = getBaseTime(time.getHour(), time.getMinute());

        time.plusHours(1);
        date = Timestamp.valueOf(time);
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH00");
        String hour = timeFormat.format(date);

        GpsTransfer gpsTransfer = new GpsTransfer();
        gpsTransfer.transfer(1, address.getX(), address.getY());

        int x = (int)Math.round(gpsTransfer.getLat());
        int y = (int)Math.round(gpsTransfer.getLng());

        return weatherClient.getWeatherInfo(
                key,
                String.valueOf(pageNo),
                String.valueOf(numOfRows),
                "JSON",
                baseDate,
                baseTime,
                String.valueOf(x),
                String.valueOf(y));
    }
}
