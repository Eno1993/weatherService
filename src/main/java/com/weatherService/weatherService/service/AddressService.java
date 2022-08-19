package com.weatherService.weatherService.service;

import com.weatherService.weatherService.domian.Address;
import com.weatherService.weatherService.repository.AddressCrud;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

@Service
public class AddressService {

    @Autowired
    private AddressCrud addressCrud;
    private final String key = "CDC17206-0BD4-35D3-8105-7A948C05C9C7";

    public int saveAllAddress(String addressStr) {

        StringBuffer buffer = new StringBuffer();
        String strResult = "";

        try {
            StringBuilder urlBuilder = new StringBuilder("http://api.vworld.kr/req/search");
            urlBuilder.append("?service=" + URLEncoder.encode("search", "UTF-8"));
            urlBuilder.append("&request=" + URLEncoder.encode("search", "UTF-8"));
            urlBuilder.append("&version=" + URLEncoder.encode("2.0", "UTF-8"));
            urlBuilder.append("&crs=" + URLEncoder.encode("EPSG:4326", "UTF-8"));
            //urlBuilder.append("&crs=" + URLEncoder.encode("EPSG:900913", "UTF-8"));
            //`urlBuilder.append("&bbox=" + URLEncoder.encode("14140071.146077,4494339.6527027,14160071.146077,4496339.6527027", "UTF-8"));
            urlBuilder.append("&size=" + URLEncoder.encode("100", "UTF-8"));
            urlBuilder.append("&page=" + URLEncoder.encode("1", "UTF-8"));
            urlBuilder.append("&query=" + URLEncoder.encode(addressStr, "UTF-8"));
            urlBuilder.append("&type=" + URLEncoder.encode("address", "UTF-8"));
            urlBuilder.append("&category=" + URLEncoder.encode("road", "UTF-8"));
            urlBuilder.append("&format=" + URLEncoder.encode("json", "UTF-8"));
            urlBuilder.append("&errorformat=" + URLEncoder.encode("json", "UTF-8"));
            urlBuilder.append("&key=" + URLEncoder.encode(key, "UTF-8"));

            URL url = new URL(urlBuilder.toString());
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            conn.setRequestMethod("GET");
            conn.setRequestProperty("Content-Type", "application/json");

            BufferedReader rd;
            if (200 <= conn.getResponseCode() && conn.getResponseCode() < 300) {
                rd = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
            } else {
                rd = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
            }

            String line;
            while (true) {
                line = rd.readLine();
                if (line == null) {
                    break;
                }
                buffer.append(line);
            }
            rd.close();
            conn.disconnect();
            strResult = buffer.toString();

        } catch (Exception e) {
            throw new IllegalStateException("서버 연결 오류.");
        }

        JSONObject jsonObject = new JSONObject(strResult);
        List<Address> addressList = new ArrayList<>();

        JSONObject response = jsonObject.getJSONObject("response");
        System.out.println("response : " + response);
        String status = response.getString("status");
        if (status.equals("NOT_FOUND")) {
            throw new IllegalStateException("주소 정보를 찾지 못했습니다.");
        }
        if (status.equals("ERROR")) {
            throw new IllegalStateException("연결 에러 발생.");
        }
        JSONObject result = response.getJSONObject("result");
        JSONArray items = result.getJSONArray("items");
        for (int i = 0; i < items.length(); i++) {
            JSONObject item = items.getJSONObject(i);
            String punCode = item.getString("id");
            JSONObject address = item.getJSONObject("address");
            int zipcode = address.getInt("zipcode");
            String road = address.getString("road");
            JSONObject point = item.getJSONObject("point");
            double x = point.getDouble("x");
            double y = point.getDouble("y");
            Address newAddress = new Address(punCode, zipcode, road, x, y);
            addressList.add(newAddress);
        }
        return addressCrud.saveAll(addressList);
    }


    public List<Address> getAllAddress() {
        return addressCrud.getAddressAll();
    }
}
