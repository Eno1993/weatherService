package com.weatherService.weatherService.repository;

import com.querydsl.jpa.impl.JPADeleteClause;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.weatherService.weatherService.domian.QSessionInfo;
import com.weatherService.weatherService.domian.SessionInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Time;
import java.sql.Timestamp;
import java.time.LocalDateTime;

@Service
public class SessionCrud extends QuerydslRepositorySupport {

    @Autowired
    private SessionRepo sessionRepo;

    @Autowired
    private JPAQueryFactory factory;

    public SessionCrud(){ super(SessionInfo.class); }

    public void save(SessionInfo sessionInfo){
        sessionRepo.save(sessionInfo);
    }

    public SessionInfo get(String sessionStr){
        Timestamp now = Timestamp.valueOf(LocalDateTime.now());

        QSessionInfo qSessionInfo = QSessionInfo.sessionInfo;
        return factory.selectFrom(qSessionInfo)
                .where(qSessionInfo.session.eq(sessionStr)
                        .and(qSessionInfo.expiredTime.after(now)))
                .fetchOne();
    }

    @Transactional
    public void sessionExpire(){
        Timestamp now = Timestamp.valueOf(LocalDateTime.now());

        QSessionInfo qSessionInfo = QSessionInfo.sessionInfo;
        JPADeleteClause delete = factory.delete(qSessionInfo)
                .where(qSessionInfo.expiredTime.before(now));
        delete.execute();
    }

}
