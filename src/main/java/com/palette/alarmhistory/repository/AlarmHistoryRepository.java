package com.palette.alarmhistory.repository;

import com.palette.alarmhistory.domain.AlarmHistory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AlarmHistoryRepository extends JpaRepository<AlarmHistory, Long> {

}
