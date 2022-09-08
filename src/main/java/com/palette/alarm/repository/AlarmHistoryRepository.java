package com.palette.alarm.repository;

import com.palette.alarm.domain.AlarmHistory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AlarmHistoryRepository extends JpaRepository<AlarmHistory, Long> {

}
