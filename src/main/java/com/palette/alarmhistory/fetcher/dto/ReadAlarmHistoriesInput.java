package com.palette.alarmhistory.fetcher.dto;

import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReadAlarmHistoriesInput {

    private List<Long> alarmHistoryIds;

}
