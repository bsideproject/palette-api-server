package com.palette.alarmhistory.service;

import com.palette.alarmhistory.domain.AlarmHistory;
import com.palette.alarmhistory.repository.AlarmHistoryRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AlarmHistoryService {

    private final AlarmHistoryRepository alarmHistoryRepository;

    public AlarmHistory createAlarmHistory(AlarmHistory alarmHistory) {
        return alarmHistoryRepository.save(alarmHistory);
    }

    public List<AlarmHistory> createAlarmHistory(List<AlarmHistory> alarmHistories) {
        return alarmHistoryRepository.saveAll(alarmHistories);
    }

}
