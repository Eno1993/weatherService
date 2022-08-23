package com.weatherService.weatherService.controller;

import com.weatherService.weatherService.client.dto.addressDto.search;
import com.weatherService.weatherService.client.dto.weatherDto.vilageFcst;
import com.weatherService.weatherService.domian.Address;
import com.weatherService.weatherService.domian.UserInfo;
import com.weatherService.weatherService.domian.Weather;
import com.weatherService.weatherService.service.AddressService;
import com.weatherService.weatherService.service.UserService;
import com.weatherService.weatherService.service.WeatherService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/weatherService/api")
public class WeatherServiceController {

    @Autowired
    private AddressService addressService;
    @Autowired
    private UserService userService;
    @Autowired
    private WeatherService weatherService;

    @PostMapping("/address/add")
    public int addAddress(@RequestParam("address") String address){
        //주소를 입력하면 여러개의 주소정보가 저장이 될 수 있음.
        //몇개의 주소가 저장이 되었는지를 반환.
        return addressService.saveAllAddress(address);
    }

    @GetMapping("/address/getAll")
    public List<Address> getAllAddress(){
        return addressService.getAllAddress();
    }

    @PostMapping("/user/add")
    public UserInfo addUser(@RequestParam(value = "userId", required = true) String userId,
                            @RequestParam(value = "passWord", required = true) String password,
                            @RequestParam(value = "email", required = true) String email,
                            @RequestParam(value = "addressId", required = true) long addressId){
        return userService.saveUser(userId, password, email, addressId);
    }

    @GetMapping("/user")
    public String getIdInfo(@RequestParam("email") String email){
        return userService.getUserIdInfo(email);
    }

    @GetMapping("/user/getUserTempCode")
    public String getTempCode(@RequestParam("userId") String userId,
                              @RequestParam("email") String email){
        return userService.getUserTempCode(userId, email);
    }

    @GetMapping("/user/getTempPassword")
    public String getUserPassword(@RequestParam("userId") String userId,
                                  @RequestParam("tempCode") String tempCode){
        return userService.getTempPassword(userId, tempCode);
    }

    @PatchMapping("/user/updateUserInfo")
    public void updateUserInfo(@RequestParam(value = "userId", required = true) String userId,
                               @RequestParam(value = "password", required = true) String password,
                               @RequestParam(value = "newUserId", required = false) String newUserId,
                               @RequestParam(value = "newPassword", required = false) String newPassword,
                               @RequestParam(value = "newEmail", required = false) String newEmail,
                               @RequestParam(value = "newAddressId", required = false) Long newAddressId){
        userService.updateUserInfo(userId, password, newUserId, newPassword, newEmail, newAddressId);
    }

    @DeleteMapping("/user/delete")
    public void deleteUser(@RequestParam("userId") String userId,
                           @RequestParam("password") String password){
        userService.deleteUser(userId, password);
    }

    @GetMapping("/user/login")
    public String login(@RequestParam("userId") String userId,
                        @RequestParam("password") String password,
                        @RequestParam("sessionExpiredTime") int minutes){
        return userService.login(userId, password, minutes);
    }

    @GetMapping("/weather/get")
    public Weather getWeatherInfo(@RequestParam("sessionInfo") String sessionInfo){
        return weatherService.getWeather(sessionInfo);
    }

    @GetMapping("/weather/get/new")
    public vilageFcst getWeatherInfoNew(
            @RequestParam(name = "sessionInfo") String sessionStr,
            @RequestParam(name = "pageNo", defaultValue = "1") int pageNo,
            @RequestParam(name = "numOfRows", defaultValue = "1000") int numOfRows){
        return weatherService.getWeatherInfo(sessionStr, pageNo, numOfRows);
    }

    @GetMapping("/address/get/new")
    public search getAddressInfoNew(@RequestParam("address") String address){
        return addressService.getAddressInfo(address);
    }

}
