package com.palette.infra.scheduler.job;

import com.google.firebase.messaging.FirebaseMessagingException;
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

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;


public class HistoryFinished implements Job {
    @Autowired
    private HistoryRepository historyRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private FcmService fcmService;

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        JobDataMap dataMap = context.getJobDetail().getJobDataMap();
        Long historyId = dataMap.getLong("historyId");
        Optional<History> historyOptional = historyRepository.findById(historyId);
        if (historyOptional.isEmpty()) return;

        History history = historyOptional.get();
        List<User> users = userRepository.findUsers(history);
        Set<String> tokens = new HashSet<>();
        users.forEach(user -> {
            if (user.getPushEnabled()) {
                tokens.addAll(user.getFcmTokens());
            }
        });
        if(tokens.isEmpty()) return;
        String body = users.get(0).getNickname() +
                "님과 " +
                users.get(1).getNickname() +
                "님이 만든 일기장이 완성됐어요.";
        Note note = Note.builder().title("일기장이 완성됐어요").body(body).build();
        try {
            fcmService.sendNotification(note, tokens);
        } catch (FirebaseMessagingException e) {
            throw new RuntimeException(e);
        }
    }
}
