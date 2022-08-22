package com.weatherService.weatherService.domian;

import com.weatherService.weatherService.domian.en.UserStatus;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.sql.Date;
import java.sql.Timestamp;

@Entity
@Getter
@NoArgsConstructor
@DiscriminatorValue(value = "user_info")
public class UserInfo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "user_id")
    private String userId;

    @Column(name = "password")
    private String password;

    @Column(name = "email")
    private String email;

    @Column(name = "address_id")
    private long addressId;

    @Column(name = "user_status")
    @Enumerated(EnumType.STRING)
    private UserStatus userStatus;

    @Column(name = "expired_period")
    private Timestamp expiredPeriod;

    @Column(name = "temp_code")
    private String tempCode;


    public UserInfo(String userId, String password, String email, long addressId){
        this.userId = userId;
        this.password = password;
        this.email = email;
        this.addressId = addressId;
        this.userStatus = UserStatus.REGISTER;
        long max = (long)Integer.MAX_VALUE*1000;
        Date date = new Date(max);
        this.expiredPeriod = new Timestamp(date.getTime());
        this.tempCode = "";
    }
}
