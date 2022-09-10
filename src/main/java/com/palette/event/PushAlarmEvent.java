package com.palette.event;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PushAlarmEvent extends Event {

    private PushAlarmEventDto pushAlarmEventDto;

    public PushAlarmEvent(PushAlarmEventDto pushAlarmEventDto) {
        super();
        this.pushAlarmEventDto = pushAlarmEventDto;
    }

}
