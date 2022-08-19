package com.weatherService.weatherService.service;

import com.weatherService.weatherService.domian.Address;
import com.weatherService.weatherService.domian.UserInfo;
import com.weatherService.weatherService.domian.en.UserStatus;
import com.weatherService.weatherService.repository.AddressCrud;
import com.weatherService.weatherService.repository.UserCrud;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class UserService {

    @Autowired
    private UserCrud userCrud;
    @Autowired
    private AddressCrud addressCrud;

    private void validateUserId(String userId){

        if(userId.length()<5){
            throw new IllegalStateException("ID는 5자리 이상으로 이루어져야 합니다.");
        }
        char c;
        for(int i=0; i<userId.length(); i++){
            c = userId.charAt(i);
            if(0x61<=c&&c<=0x7A){
                ;
            }else if(0x41<=c&&c<=0x5A){
                ;
            }else if(0x30<=c&&c<=0x39){
                ;
            }else{
                throw new IllegalStateException("ID는 영문과 숫자로만 이루어져야 합니다.");
            }
        }
        long overLapCount = userCrud.validateUserId(userId);
        if(1<=overLapCount){
            throw new IllegalStateException("이미 등록된 ID 입니다.");
        }
    }
    private void validatePassWord(String password){

        if(password.length()<8){
            throw new IllegalStateException("PASSWORD는 8자리 이상이어야 합니다.");
        }
        for(int i=0; i<password.length(); i++){
            if(String.valueOf(password.charAt(i)).matches("[^a-zA-Z0-9\\\\s]")){
                return;
            }
        }
        throw new IllegalStateException("PASSWORD는 특수문자를 포함하고 있어야 합니다.");
    }
    private void validateEmail(String email) {
        String regx = "^(.+)@(.+)$";
        Pattern pattern = Pattern.compile(regx);
        Matcher mathcer = pattern.matcher(email);
        if(!mathcer.matches()){
            throw new IllegalStateException("EMAIL 형식이 아닙니다.");
        }
        long overLapCount = userCrud.validateUserEmail(email);
        if(1<=overLapCount){
            throw new IllegalStateException("이미 등록된 EMAIL 입니다.");
        }
    }
    private void validateAddress(long addressId){
        Address address = addressCrud.getAddressById(addressId);
        if(address==null){
            throw new IllegalStateException("존재하지 않은 주소 ID 입니다.");
        }
    }

    public UserInfo saveUser(String userId, String password, String email, long addressId){

        validateUserId(userId);
        validatePassWord(password);
        validateEmail(email);
        validateAddress(addressId);

        return userCrud.save(userId, password, email, addressId);
    }

    public String getUserIdInfo(String email) {
        //validateEmail(email);
        UserInfo user = userCrud.getUserByEmail(email);
        if(user==null){
            throw new IllegalStateException("등록된 EMAIL이 없습니다.");
        }
        String str = user.getUserId();
        return str.substring(0,4);
    }

    public String getUserTempCode(String userId, String email) {
        UserInfo user = userCrud.getUserByIdByEmail(userId, email);
        if(user==null){
            throw new IllegalStateException("등록된 사용자 정보가 없습니다.");
        }
        String tempCode = getRandomStr(15);
        userCrud.updateUserTempCode(user.getUserId(), tempCode);

        Timer timer = new Timer(true);
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                userCrud.checkUserStatus(userId);
            }
        };

        timer.schedule(task, 1000*60*3);
        return tempCode;
    }

    public String getTempPassword(String userId, String tempCode){
        UserInfo user = userCrud.getUserByUserId(userId);
        if(user==null){
            throw new IllegalStateException("등록된 사용자 정보가 없습니다.");
        }
        if(user.getUserStatus()==UserStatus.REGISTER){
            throw new IllegalStateException("임시코드를 먼저 발급받으세요.");
        }
        if(user.getUserStatus()==UserStatus.EXPIRED){
            throw new IllegalStateException("임시코드 기간이 만료되었습니다.");
        }
        if(!user.getTempCode().equals(tempCode)){
            throw new IllegalStateException("임시코드를 잘못 입력했습니다.");
        }
        String tempPassword = getRandomStr(15);
        userCrud.updateTempPassword(userId, tempPassword);
        return tempPassword;
    }

    private String getRandomStr(int strLen){
        char[] charSet = new char[] {
                '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
                'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z',
                'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z',
                '!', '@', '#', '$', '%', '^', '&' };

        StringBuffer sb = new StringBuffer();
        SecureRandom sr = new SecureRandom();
        sr.setSeed(new Date().getTime());

        int idx = 0;
        int len = charSet.length;
        for (int i=0; i<strLen; i++) {
            idx = sr.nextInt(len);
            sb.append(charSet[idx]);
        }

        return sb.toString();
    }

    public void updateUserInfo(String userId, String password, String newUserId, String newPassword, String newEmail, Long newAddressId) {

        UserInfo user = userCrud.getUserByUserIdByPassword(userId, password);
        if(user==null){
            throw new IllegalStateException("잘못된 ID 혹은 PASSWORD 입니다.");
        }
        if(user.getUserStatus()==UserStatus.EXPIRED){
            throw new IllegalStateException("만료된 ID 입니다.");
        }
        if(user.getUserStatus()==UserStatus.UPDATING){
            if(newUserId!=null||newPassword==null||newEmail!=null||newAddressId!=null){
                throw new IllegalStateException("임시 비밀번호를 먼저 변경하세요.");
            }
        }
        if(newUserId!=null){
            validateUserId(newUserId);
        }
        if(newPassword!=null){
            validatePassWord(newPassword);
        }
        if(newEmail!=null){
            validateEmail(newEmail);
        }
        if(newAddressId!=null){
            validateAddress(newAddressId);
        }

        userCrud.updateUserInfo(userId, newUserId, newPassword, newEmail, newAddressId);

    }

    public void deleteUser(String userId, String password) {
        UserInfo user = userCrud.getUserByUserIdByPassword(userId, password);
        if(user==null){
            throw new IllegalStateException("등록된 사용자 정보가 없습니다.");
        }
        userCrud.deleteUser(userId);
    }

    public String login(String userId, String password) {
        UserInfo user = userCrud.getUserByUserIdByPassword(userId, password);
        if(user==null){
            throw new IllegalStateException("등록된 사용자 정보가 없습니다.");
        }
        String sessionInfo = getRandomStr(15);
        userCrud.setUserSessionInfo(userId, sessionInfo);

        Timer timer = new Timer(true);
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                userCrud.setUserSessionInfo(userId, null);
            }
        };
        timer.schedule(task, 1000*60*30);

        return sessionInfo;
    }
}
