package com.palette.infra.fcm;

import com.google.firebase.messaging.FirebaseMessagingException;
import com.palette.alarmhistory.domain.AlarmHistory;
import com.palette.alarmhistory.service.AlarmHistoryService;
import com.palette.diary.domain.Diary;
import com.palette.diary.domain.History;
import com.palette.diary.domain.Page;
import com.palette.exception.graphql.UserNotFoundException;
import com.palette.user.domain.User;
import com.palette.user.repository.UserRepository;
import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class PushNotificationService {

    private UserRepository userRepository;
    private FcmService fcmService;
    private AlarmHistoryService alarmHistoryService;

    public void diaryCreated(Diary diary, List<User> users) throws FirebaseMessagingException {
        Set<String> tokens = fcmService.getTokens(users);
        if (tokens.isEmpty()) {
            return;
        }

        Map<String, String> noteData = new HashMap<>();
        noteData.put("page", "home");
        noteData.put("diaryId", diary.getId().toString());

        String title = "'" + diary.getTitle() + "'" + " 일기장이 생성되었어요.";
        String body = "첫 교환일기를 시작해보세요!";

        fcmService.sendNotification(createNote(title, body, noteData), tokens);

        StringBuilder historyBody = new StringBuilder();
        historyBody.append(title);
        historyBody.append(body);

        alarmHistoryService.createAlarmHistory(
            toEntities(users, historyBody.toString(), "home", diary.getId(), null)
        );

    }

    public void historyCreated(History history) throws FirebaseMessagingException {
        List<User> users = userRepository.findUsers(history);
        Set<String> tokens = fcmService.getTokens(users);
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

        fcmService.sendNotification(createNote(noteTitle, noteBody, noteData), tokens);

        StringBuilder historyBody = new StringBuilder();
        historyBody.append(noteTitle);
        historyBody.append(noteBody);

        alarmHistoryService.createAlarmHistory(
            toEntities(users, historyBody.toString(), "history",
                history.getDiary().getId(), history.getId())
        );

    }

    public void pageCreated(Page page) throws FirebaseMessagingException {
        History history = page.getHistory();
        Diary diary = history.getDiary();
        List<User> users = userRepository.findUsers(history);
        User author = users.stream().filter(user -> user.getId().equals(page.getUserId()))
            .findFirst().orElseThrow(UserNotFoundException::new);
        users.remove(author);

        HashSet<String> tokens = fcmService.getTokens(users);
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

        fcmService.sendNotification(createNote(noteTitle, noteBody, noteData), tokens);

        alarmHistoryService.createAlarmHistory(
            toEntities(users, historyBody.toString(), "history", diary.getId(),
                history.getId())
        );

    }

    public void historyFinished(History history) throws FirebaseMessagingException {
        Diary diary = history.getDiary();
        List<User> users = userRepository.findUsers(history);

        Set<String> tokens = fcmService.getTokens(users);
        if (tokens.isEmpty()) {
            return;
        }

        String noteTitle = "일기가 완성됐어요!";
        String noteBody = diary.getTitle() + " 일기가 완성됐어요.";

        Map<String, String> noteData = new HashMap<>();
        noteData.put("page", "history");
        noteData.put("diaryId", diary.getId().toString());
        noteData.put("historyId", history.getId().toString());

        fcmService.sendNotification(createNote(noteTitle, noteBody, noteData), tokens);

        StringBuilder historyBody = new StringBuilder();
        historyBody.append(noteTitle);

        alarmHistoryService.createAlarmHistory(
            toEntities(users, historyBody.toString(), "history", diary.getId(),
                history.getId())
        );

    }

    private Note createNote(String noteTitle, String noteBody, Map<String, String> noteData) {
        return Note.builder()
            .title(noteTitle)
            .body(noteBody)
            .data(noteData)
            .build();
    }

    private List<AlarmHistory> toEntities(List<User> users, String body, String movePage,
        Long diaryId,
        Long historyId) {
        List<AlarmHistory> alarmHistories = new ArrayList<>();
        for (User user : users) {
            alarmHistories.add(AlarmHistory.builder()
                .user(user)
                .body(body)
                .movePage(movePage)
                .diaryId(diaryId)
                .historyId(historyId)
                .build());
        }
        return alarmHistories;
    }

}
