package com.weatherService.weatherService.repository;

import com.weatherService.weatherService.domian.UserInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepo extends JpaRepository<UserInfo, Long> {

}
