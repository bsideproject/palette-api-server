package com.palette.event;

import com.palette.diary.domain.Diary;
import com.palette.diary.domain.History;
import com.palette.diary.domain.Page;
import java.util.List;

import com.palette.user.domain.User;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@ToString
@Getter
@Setter
@Builder
public class PushAlarmEventDto {

    private EventsKind eventsKind;
    private History history;
    private User user;
    private List<Long> userIds;
    private Diary diary;
    private List<Diary> outDiaries;
    private Page page;

}
