package com.palette.alarm.service;

import com.palette.alarm.domain.AlarmHistory;
import com.palette.alarm.repository.AlarmHistoryRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AlarmHistoryService {

    private final AlarmHistoryRepository alarmHistoryRepository;

    public void createAlarmHistory(List<AlarmHistory> alarmHistories) {
        alarmHistoryRepository.saveAll(alarmHistories);
    }

}
