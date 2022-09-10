package com.palette.event;

import com.palette.diary.domain.Diary;
import com.palette.diary.domain.History;
import com.palette.diary.domain.Page;
import com.palette.user.domain.User;
import java.util.List;
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
    private List<User> users;
    private Diary diary;
    private Page page;
    private Long loginUserId;

}
