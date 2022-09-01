package com.palette.infra.scheduler.job;

import com.google.firebase.messaging.FirebaseMessagingException;
import com.palette.diary.domain.Diary;
import com.palette.diary.domain.History;
import com.palette.diary.repository.HistoryRepository;
import com.palette.infra.fcm.FcmService;
import com.palette.infra.fcm.Note;
import com.palette.user.domain.User;
import com.palette.user.repository.UserRepository;
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
    private UserRepository userRepository;

    @Autowired
    private FcmService fcmService;

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
            Diary diary = history.getDiary();
            List<User> users = userRepository.findUsers(history);

            Set<String> tokens = new HashSet<>();
            users.forEach(user -> {
                if (user.getPushEnabled()) {
                    tokens.addAll(user.getFcmTokens());
                }
            });
            if (tokens.isEmpty()) return;

            String noteTitle = "일기가 완성됐어요!";
            String noteBody = diary.getTitle() + " 일기가 완성됐어요.";
            Map<String, String> noteData = new HashMap<>();
            noteData.put("page", "history");
            noteData.put("historyId", history.getId().toString());
            Note note = Note.builder().title(noteTitle).body(noteBody).data(noteData).build();

            try {
                fcmService.sendNotification(note, tokens);
            } catch (FirebaseMessagingException e) {
                throw new RuntimeException(e);
            }
        }
        catch (Exception ex) {
            transactionManager.rollback(status);
            throw ex;
        }
        transactionManager.commit(status);
    }
}
