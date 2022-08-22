package com.weatherService.weatherService.repository;

import com.weatherService.weatherService.domian.SessionInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.RequestParam;

@Repository
public interface SessionRepo extends JpaRepository<SessionInfo, Long> {
}
