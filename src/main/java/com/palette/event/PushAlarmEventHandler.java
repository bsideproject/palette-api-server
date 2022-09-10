package com.palette.event;

import com.palette.infra.fcm.PushNotificationService;
import io.sentry.Sentry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Slf4j
@Component
@RequiredArgsConstructor
public class PushAlarmEventHandler {

    private final PushNotificationService service;

    @Async("threadPoolTaskExecutor")
    @TransactionalEventListener(
        classes = PushAlarmEvent.class,
        phase = TransactionPhase.AFTER_COMMIT
    )
    public void handle(PushAlarmEvent event) {
        PushAlarmEventDto eventDto = event.getPushAlarmEventDto();
        log.info("Push Event Timestamp: {}", event.getTimestamp());
        log.info("Push Event Kind: {}", eventDto);
        try {
            switch (eventDto.getEventsKind()) {
                case CREATE_DIARY -> service.createDiary(eventDto.getDiary(), eventDto.getUsers());
                case CREATE_HISTORY -> service.createHistory(eventDto.getHistory());
                case CREATE_PAGE -> service.createPage(eventDto.getPage());
            }
        } catch (Exception e) {
            Sentry.captureException(e);
        }
    }

}
