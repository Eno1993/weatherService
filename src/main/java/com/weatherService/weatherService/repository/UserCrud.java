package com.weatherService.weatherService.repository;

import com.querydsl.jpa.JPQLQuery;
import com.querydsl.jpa.impl.JPADeleteClause;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.querydsl.jpa.impl.JPAUpdateClause;
import com.weatherService.weatherService.domian.QUserInfo;
import com.weatherService.weatherService.domian.UserInfo;
import com.weatherService.weatherService.domian.en.UserStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Date;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Calendar;

@Service
public class UserCrud extends QuerydslRepositorySupport {

    @Autowired
    private UserRepo userRepo;
    @Autowired
    private JPAQueryFactory factory;

    public UserCrud(){ super(UserInfo.class); }

    public UserInfo save(String userId, String password, String email, long addressId){
        UserInfo userInfo = new UserInfo(userId, password, email, addressId);
        userRepo.save(userInfo);
        return userInfo;
    }

    public UserInfo getUserByUserId(String userId){
        QUserInfo qUserInfo = QUserInfo.userInfo;
        JPQLQuery<UserInfo> query = factory.selectFrom(qUserInfo)
                .where(qUserInfo.userId.eq(userId));
        return query.fetchOne();
    }

    public long validateUserId(String userId){
        QUserInfo qUserInfo = QUserInfo.userInfo;
        JPQLQuery<UserInfo> query = factory.selectFrom(qUserInfo)
                .where(qUserInfo.userId.eq(userId));
        return query.fetchCount();
    }

    public long validateUserEmail(String email){
        QUserInfo qUserInfo = QUserInfo.userInfo;
        JPQLQuery<UserInfo> query = factory.selectFrom(qUserInfo)
                .where(qUserInfo.email.eq(email));
        return query.fetchCount();
    }

    public UserInfo getUserByEmail(String email) {
        QUserInfo qUserInfo = QUserInfo.userInfo;
        return factory.selectFrom(qUserInfo)
                .where(qUserInfo.email.eq(email))
                .fetchOne();
    }

    public UserInfo getUserByIdByEmail(String userId, String email) {
        QUserInfo qUserInfo = QUserInfo.userInfo;
        return factory.selectFrom(qUserInfo)
                .where(qUserInfo.userId.eq(userId).and(qUserInfo.email.eq(email)))
                .fetchOne();
    }

    @Transactional
    public void updateUserTempCode(String userId, String tempCode) {

        Timestamp timestamp = Timestamp.valueOf(LocalDateTime.now());

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(timestamp);
        calendar.add(Calendar.MINUTE, 3);

        timestamp.setTime(calendar.getTime().getTime());

        QUserInfo qUserInfo = QUserInfo.userInfo;
        JPAUpdateClause update = factory.update(qUserInfo)
                .where(qUserInfo.userId.eq(userId))
                .set(qUserInfo.tempCode, tempCode)
                .set(qUserInfo.userStatus,UserStatus.UPDATING)
                .set(qUserInfo.expiredPeriod, timestamp);
        update.execute();
    }

    @Transactional
    public void expireUserStatus(String userId) {

        //해당 유저의 상태를 확인하고 expired_period 가 현재 시간보다 더 이전일 경우 해당유저의 정보를 expire 한다.
        Timestamp  now = Timestamp.valueOf(LocalDateTime.now());

        QUserInfo qUserInfo = QUserInfo.userInfo;
        JPAUpdateClause update = factory.update(qUserInfo)
                .where(qUserInfo.userId.eq(userId)
                        .and(qUserInfo.expiredPeriod.before(now)))
                .set(qUserInfo.userStatus, UserStatus.EXPIRED);
        update.execute();
    }

    @Transactional
    public void updateTempPassword(String userId, String tempPassword) {

        QUserInfo qUserInfo = QUserInfo.userInfo;
        JPAUpdateClause update = factory.update(qUserInfo)
                .where(qUserInfo.userId.eq(userId))
                .set(qUserInfo.password, tempPassword);
        update.execute();
    }

    public UserInfo getUserByUserIdByPassword(String userId, String password) {
        QUserInfo qUserInfo = QUserInfo.userInfo;
        return factory.selectFrom(qUserInfo)
                .where(qUserInfo.userId.eq(userId).and(qUserInfo.password.eq(password)))
                .fetchOne();
    }

    @Transactional
    public void updateUser(String userId, String newUserId, String newPassword, String newEmail, Long newAddressId){

        QUserInfo qUserInfo = QUserInfo.userInfo;
        JPAUpdateClause update = factory.update(qUserInfo)
                .where(qUserInfo.userId.eq(userId));

        if(newUserId!=null){
            update.set(qUserInfo.userId, newUserId);
        }
        if(newPassword!=null){
            long max = (long)Integer.MAX_VALUE*1000;
            Date date = new Date(max);
            Timestamp timestamp = new Timestamp(date.getTime());

            update.set(qUserInfo.password, newPassword)
                    .set(qUserInfo.userStatus, UserStatus.REGISTER)
                    .set(qUserInfo.expiredPeriod, timestamp)
                    .set(qUserInfo.tempCode, "");
        }
        if(newEmail!=null){
            update.set(qUserInfo.email, newEmail);
        }
        if(newAddressId!=null){
            update.set(qUserInfo.addressId, newAddressId);
        }
        update.execute();
    }

    @Transactional
    public void deleteUser(String userId) {

        QUserInfo qUserInfo = QUserInfo.userInfo;
        JPADeleteClause delete = factory.delete(qUserInfo)
                .where(qUserInfo.userId.eq(userId));
        delete.execute();
    }

    public UserInfo getUserById(long id){
        QUserInfo qUserInfo = QUserInfo.userInfo;
        return factory.selectFrom(qUserInfo)
                .where(qUserInfo.id.eq(id))
                .fetchOne();
    }

}

