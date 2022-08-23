package com.weatherService.weatherService.service;


import com.weatherService.weatherService.domian.SessionInfo;
import com.weatherService.weatherService.repository.SessionCrud;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.stereotype.Service;
import javax.annotation.PostConstruct;

@Service
public class SessionService {

    @Autowired
    private SessionCrud sessionCrud;
    private ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();

    @PostConstruct
    public void startScheduler(){
        scheduler.initialize();
        scheduler.setPoolSize(1);

        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                sessionCrud.sessionExpire();
            }
        };
        scheduler.schedule(runnable, new CronTrigger("0 0 0/1 * * *"));
    }
}
