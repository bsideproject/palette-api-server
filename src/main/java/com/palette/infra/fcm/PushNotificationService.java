package com.palette.infra.fcm;

import com.google.firebase.messaging.FirebaseMessagingException;
import com.palette.alarmhistory.domain.AlarmHistory;
import com.palette.alarmhistory.service.AlarmHistoryService;
import com.palette.diary.domain.Diary;
import com.palette.diary.domain.History;
import com.palette.diary.domain.Page;
import com.palette.exception.graphql.UserNotFoundExceptionForGraphQL;
import com.palette.user.domain.User;
import com.palette.user.repository.UserRepository;
import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
@Transactional
@AllArgsConstructor
public class PushNotificationService {

    private UserRepository userRepository;
    private FcmService fcmService;
    private AlarmHistoryService alarmHistoryService;

    public void createDiary(Diary diary, List<User> users) throws FirebaseMessagingException {
        log.info("createDiary call");
        Map<User, Set<String>> fcmTokens = getFcmTokens(users);

        for (User user : fcmTokens.keySet()) {
            Set<String> tokens = fcmTokens.get(user);
            if (tokens.isEmpty()) {
                return;
            }

            Map<String, String> noteData = new HashMap<>();
            noteData.put("page", "home");
            noteData.put("diaryId", diary.getId().toString());

            String title = "'" + diary.getTitle() + "'" + " 일기장이 생성되었어요.";
            String body = "첫 교환일기를 시작해보세요!";

            StringBuilder historyBody = new StringBuilder();
            historyBody.append(title);
            historyBody.append(body);

            AlarmHistory alarmHistory = alarmHistoryService.createAlarmHistory(
                toEntity(user, historyBody.toString(), "home", diary.getId(), null)
            );
            noteData.put("alarmHistoryId", alarmHistory.getId().toString());

            String resultMessage = fcmService.sendNotification(createNote(title, body, noteData),
                tokens);
            log.info("fcm resultMessage: {} ", resultMessage);
        }

    }

    public void createHistory(History history) throws FirebaseMessagingException {
        log.info("createHistory call");
        List<User> users = userRepository.findUsers(history);
        Map<User, Set<String>> fcmTokens = getFcmTokens(users);

        for (User user : fcmTokens.keySet()) {
            Set<String> tokens = fcmTokens.get(user);
            if (tokens.isEmpty()) {
                return;
            }

            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < users.size(); i++) {
                sb.append(users.get(i).getNickname()).append("님");
                if (i == users.size()) {
                    sb.append("의");
                } else {
                    sb.append("과");
                }
            }
            sb.append(" 교환일기가 시작됐어요.");

            String noteTitle = sb.toString();
            String noteBody =
                Duration.between(history.getStartDate(), history.getEndDate()).toDays()
                    + "일 후 상대방에게 보여주고 싶은 나의 하루를 기록해보세요.";

            Map<String, String> noteData = new HashMap<>();
            noteData.put("page", "history");
            noteData.put("diaryId", history.getDiary().getId().toString());
            noteData.put("historyId", history.getId().toString());

            StringBuilder historyBody = new StringBuilder();
            historyBody.append(noteTitle);
            historyBody.append(noteBody);

            AlarmHistory alarmHistory = alarmHistoryService.createAlarmHistory(
                toEntity(user, historyBody.toString(), "history",
                    history.getDiary().getId(), history.getId())
            );

            noteData.put("alarmHistoryId", alarmHistory.getId().toString());

            String resultMessage = fcmService.sendNotification(
                createNote(noteTitle, noteBody, noteData),
                tokens);
            log.info("fcm resultMessage: {} ", resultMessage);
        }

    }

    public void createPage(Page page) throws FirebaseMessagingException {
        log.info("createPage call");

        History history = page.getHistory();
        Diary diary = history.getDiary();
        List<User> users = userRepository.findUsers(history);

        User author = users.stream()
            .filter(user -> user.getId().equals(page.getUserId()))
            .findFirst().orElseThrow(UserNotFoundExceptionForGraphQL::new);
        users.remove(author);

        Set<String> tokens = fcmService.getTokens(users);
        if (tokens.isEmpty()) {
            return;
        }

        String noteTitle = author.getNickname() + "님이 " + "에 오늘의 일기를 작성했어요.";
        String noteBody = "";

        Map<String, String> noteData = new HashMap<>();
        noteData.put("page", "history");
        noteData.put("diaryId", diary.getId().toString());
        noteData.put("historyId", history.getId().toString());

        StringBuilder historyBody = new StringBuilder();
        historyBody.append(noteTitle);

        AlarmHistory alarmHistory = alarmHistoryService.createAlarmHistory(
            toEntity(users.get(0), historyBody.toString(), "history", diary.getId(),
                history.getId())
        );

        noteData.put("alarmHistoryId", alarmHistory.getId().toString());

        String resultMessage = fcmService.sendNotification(
            createNote(noteTitle, noteBody, noteData), tokens);
        log.info("fcm resultMessage: {} ", resultMessage);
    }

    public void finishHistory(History history) throws FirebaseMessagingException {
        log.info("finishHistory call");

        Diary diary = history.getDiary();
        List<User> users = userRepository.findUsers(history);

        Map<User, Set<String>> fcmTokens = getFcmTokens(users);

        for (User user : fcmTokens.keySet()) {
            Set<String> tokens = fcmTokens.get(user);
            if (tokens.isEmpty()) {
                return;
            }

            String noteTitle = "일기가 완성됐어요!";
            String noteBody = diary.getTitle() + " 일기가 완성됐어요.";

            Map<String, String> noteData = new HashMap<>();
            noteData.put("page", "history");
            noteData.put("diaryId", diary.getId().toString());
            noteData.put("historyId", history.getId().toString());

            StringBuilder historyBody = new StringBuilder();
            historyBody.append(noteTitle);

            AlarmHistory alarmHistory = alarmHistoryService.createAlarmHistory(
                toEntity(user, historyBody.toString(), "history", diary.getId(),
                    history.getId())
            );

            noteData.put("alarmHistoryId", alarmHistory.getId().toString());

            String resultMessage = fcmService.sendNotification(
                createNote(noteTitle, noteBody, noteData),
                tokens);
            log.info("fcm resultMessage: {} ", resultMessage);
        }

    }

    private Note createNote(String noteTitle, String noteBody, Map<String, String> noteData) {
        return Note.builder()
            .title(noteTitle)
            .body(noteBody)
            .data(noteData)
            .build();
    }

    private AlarmHistory toEntity(User user, String body, String movePage, Long diaryId,
        Long historyId) {
        return AlarmHistory.builder()
            .user(user)
            .body(body)
            .movePage(movePage)
            .diaryId(diaryId)
            .historyId(historyId)
            .build();
    }

    private Map<User, Set<String>> getFcmTokens(List<User> users) {
        Map<User, Set<String>> fcmTokens = new HashMap<>();
        for (User user : users) {
            fcmTokens.put(user, user.getFcmTokens());
        }
        return fcmTokens;
    }

}
