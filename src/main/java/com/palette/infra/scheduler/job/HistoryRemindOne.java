package com.palette.infra.scheduler.job;

import com.google.firebase.messaging.FirebaseMessagingException;
import com.palette.diary.domain.History;
import com.palette.diary.repository.HistoryRepository;
import com.palette.infra.fcm.PushNotificationService;
import io.sentry.Sentry;
import java.util.Optional;
import javax.annotation.Resource;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

@Slf4j
@Component
@RequiredArgsConstructor
public class HistoryRemindOne implements Job {

    @Resource
    private PlatformTransactionManager transactionManager;
    private final HistoryRepository historyRepository;
    private final PushNotificationService pushNotificationService;

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        log.info("HistoryRemindOne Job execute");

        DefaultTransactionDefinition def = new DefaultTransactionDefinition();
        def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);
        TransactionStatus status = transactionManager.getTransaction(def);

        try {
            JobDataMap dataMap = context.getJobDetail().getJobDataMap();
            Long historyId = Long.parseLong(dataMap.getString("historyId"));
            Optional<History> historyOptional = historyRepository.findById(historyId);
            if (historyOptional.isEmpty()) {
                return;
            }

            History history = historyOptional.get();
            pushNotificationService.remindHistoryOne(history);
        } catch (FirebaseMessagingException e) {
            Sentry.captureException(e);
            throw new RuntimeException(e);
        } catch (Exception ex) {
            Sentry.captureException(ex);
            transactionManager.rollback(status);
            throw ex;
        }
        transactionManager.commit(status);
    }
}
