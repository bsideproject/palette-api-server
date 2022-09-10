package com.palette.alarmhistory.repository;

import com.palette.alarmhistory.domain.AlarmHistory;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface AlarmHistoryRepository extends JpaRepository<AlarmHistory, Long> {

    @Modifying
    @Query("update AlarmHistory ah set ah.isRead = true where ah.id in :ids")
    int readAlarmHistories(@Param("ids") List<Long> ids);
}
