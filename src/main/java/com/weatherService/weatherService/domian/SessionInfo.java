package com.weatherService.weatherService.domian;

import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Calendar;

@Entity
@Getter
@NoArgsConstructor
@DiscriminatorValue(value = "session_info")
public class SessionInfo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "session_string")
    private String sessionStr;

    @Column(name = "expired_time")
    private Timestamp expiredTime;

    @Column(name = "user_id")
    private long userId;

    public SessionInfo(long id, String sessionStr, int minutes){
        this.userId = id;
        this.sessionStr = sessionStr;

        Timestamp timestamp = Timestamp.valueOf(LocalDateTime.now());
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(timestamp);
        calendar.add(Calendar.MINUTE, minutes);
        timestamp.setTime(calendar.getTime().getTime());

        this.expiredTime = timestamp;
    }
}
