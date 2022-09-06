package com.palette.infra.scheduler.job;

import com.google.firebase.messaging.FirebaseMessagingException;
import com.palette.diary.domain.History;
import com.palette.diary.repository.HistoryRepository;
import com.palette.infra.fcm.PushNotificationService;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import javax.annotation.Resource;
import java.util.*;

@Component
public class HistoryFinished implements Job {
    @Resource
    private PlatformTransactionManager transactionManager;
    @Autowired
    private HistoryRepository historyRepository;

    @Autowired
    private PushNotificationService pushNotificationService;

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        DefaultTransactionDefinition def = new DefaultTransactionDefinition();
        def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);
        TransactionStatus status = transactionManager.getTransaction(def);

        try {
            JobDataMap dataMap = context.getJobDetail().getJobDataMap();
            Long historyId = Long.parseLong(dataMap.getString("historyId"));
            Optional<History> historyOptional = historyRepository.findById(historyId);
            if (historyOptional.isEmpty()) return;

            History history = historyOptional.get();
            pushNotificationService.historyFinished(history);
        } catch (FirebaseMessagingException e) {
            throw new RuntimeException(e);
        } catch (Exception ex) {
            transactionManager.rollback(status);
            throw ex;
        }
        transactionManager.commit(status);
    }
}
