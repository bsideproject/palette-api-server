package com.palette.infra.fcm;

import com.google.firebase.messaging.BatchResponse;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.palette.alarmhistory.domain.AlarmHistory;
import com.palette.alarmhistory.service.AlarmHistoryService;
import com.palette.diary.domain.Diary;
import com.palette.diary.domain.DiaryGroup;
import com.palette.diary.domain.History;
import com.palette.diary.domain.Page;
import com.palette.diary.repository.DiaryGroupRepository;
import com.palette.exception.graphql.DiaryNotFoundException;
import com.palette.exception.graphql.UserNotFoundExceptionForGraphQL;
import com.palette.user.domain.User;
import com.palette.user.repository.UserRepository;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
@Transactional
@RequiredArgsConstructor
public class PushNotificationService {

    private final UserRepository userRepository;
    private final FcmService fcmService;
    private final AlarmHistoryService alarmHistoryService;

    private final DiaryGroupRepository diaryGroupRepository;

    public void createDiary(Diary diary, List<Long> userIds) throws FirebaseMessagingException {
        log.info("createDiary call");
        List<User> users = userRepository.findAllById(userIds);
        Map<User, Set<String>> fcmTokens = getFcmTokens(users);
        log.info("createDiary of fcmTokens: {}", fcmTokens);

        for (User user : fcmTokens.keySet()) {
            Set<String> tokens = fcmTokens.get(user);
            if (tokens.isEmpty()) {
                return;
            }

            Map<String, String> noteData = new HashMap<>();
            noteData.put("page", "home");
            noteData.put("diaryId", diary.getId().toString());

            String title = "'" + diary.getTitle() + "'" + " 일기장이 생성되었어요. ";
            String body = "첫 교환일기를 시작해보세요!";

            StringBuilder historyBody = new StringBuilder();
            historyBody.append(title);
            historyBody.append(body);

            AlarmHistory alarmHistory = alarmHistoryService.createAlarmHistory(
                toEntity(user, historyBody.toString(), "home", diary.getId(), null)
            );
            noteData.put("alarmHistoryId", alarmHistory.getId().toString());

            BatchResponse batchResponse = fcmService.sendNotification(
                createNote(title, body, noteData),
                tokens);

            log.info("createDiary push fcm successCount: {}", batchResponse.getSuccessCount());
            log.info("createDiary push fcm failCount: {}", batchResponse.getFailureCount());
            log.info("createDiary push fcm messageId {}",
                batchResponse.getResponses().stream()
                    .peek(response -> {
                        log.info("messageId: {}", response.getMessageId());
                    })
            );
        }

    }

    public void outDiary(Diary diary, User outUser) throws FirebaseMessagingException {
        log.info("outDiary call");
        List<DiaryGroup> diaryGroups = diaryGroupRepository.findContainsUser(diary)
            .orElseThrow(DiaryNotFoundException::new);
        List<User> users = diaryGroups.stream().map(DiaryGroup::getUser).toList();
        Map<User, Set<String>> fcmTokens = getFcmTokens(users);
        log.info("outDiary of fcmTokens: {}", fcmTokens);

        Map<String, String> noteData = new HashMap<>();
        noteData.put("page", "history");
        noteData.put("diaryId", diary.getId().toString());
        String title = outUser.getNickname() + "님이 " + diary.getTitle() + " 일기장을 나갔어요.";
        String body = "";

        StringBuilder historyBody = new StringBuilder();
        historyBody.append(title);
        historyBody.append(body);

        for (User user : fcmTokens.keySet()) {
            if (Objects.equals(outUser.getId(), user.getId())) {
                noteData.put("isOut", "1");
            } else {
                noteData.put("isOut", "0");
            }

            AlarmHistory alarmHistory = alarmHistoryService.createAlarmHistory(
                toEntity(user, historyBody.toString(), "home", diary.getId(), null)
            );
            noteData.put("alarmHistoryId", alarmHistory.getId().toString());

            Set<String> tokens = fcmTokens.get(user);
            if (tokens.isEmpty()) {
                return;
            }

            BatchResponse batchResponse = fcmService.sendNotification(
                createNote(title, body, noteData),
                tokens);

            log.info("outDiary push fcm successCount: {}", batchResponse.getSuccessCount());
            log.info("outDiary push fcm failCount: {}", batchResponse.getFailureCount());
            log.info("outDiary push fcm messageId {}",
                batchResponse.getResponses().stream()
                    .peek(response -> {
                        log.info("messageId: {}", response.getMessageId());
                    })
            );
        }

    }

    @Transactional
    public void createHistory(History history) throws FirebaseMessagingException {
        log.info("createHistory call");
        List<User> users = userRepository.findUsers(history);
        Map<User, Set<String>> fcmTokens = getFcmTokens(users);
        log.info("createHistory of fcmTokens: {}", fcmTokens);

        for (User user : fcmTokens.keySet()) {
            Set<String> tokens = fcmTokens.get(user);
            if (tokens.isEmpty()) {
                return;
            }

            StringBuilder sb = new StringBuilder();
            // 일기 그룹에 2명 제한된 조건전용 로직
            for (int i = 0; i < users.size(); i++) {
                if (users.size() - 1 == i) {
                    sb.append(" ");
                }
                sb.append(users.get(i).getNickname()).append("님");
                if (i == users.size() - 1) {
                    sb.append("의");
                } else {
                    sb.append("과");
                }
            }
            sb.append(" 교환일기가 시작됐어요. ");

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

            BatchResponse batchResponse = fcmService.sendNotification(
                createNote(noteTitle, noteBody, noteData),
                tokens);

            log.info("createHistory push fcm successCount: {}", batchResponse.getSuccessCount());
            log.info("createHistory push fcm failCount: {}", batchResponse.getFailureCount());
            log.info("createHistory push fcm messageId {}",
                batchResponse.getResponses().stream()
                    .peek(response -> {
                        log.info("messageId: {}", response.getMessageId());
                    })
            );
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
        log.info("createPage of fcmTokens: {}", tokens);
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

        BatchResponse batchResponse = fcmService.sendNotification(
            createNote(noteTitle, noteBody, noteData), tokens);

        log.info("createPage push fcm successCount: {}", batchResponse.getSuccessCount());
        log.info("createPage push fcm failCount: {}", batchResponse.getFailureCount());
        log.info("createPage push fcm messageId {}",
            batchResponse.getResponses().stream()
                .peek(response -> {
                    log.info("messageId: {}", response.getMessageId());
                })
        );
    }

    public void finishHistory(History history) throws FirebaseMessagingException {
        log.info("finishHistory call");

        Diary diary = history.getDiary();
        List<User> users = userRepository.findUsers(history);

        Map<User, Set<String>> fcmTokens = getFcmTokens(users);
        log.info("finishHistory of fcmTokens: {}", fcmTokens);

        for (User user : fcmTokens.keySet()) {
            Set<String> tokens = fcmTokens.get(user);
            if (tokens.isEmpty()) {
                return;
            }

            User otherUser = users.stream()
                .filter(paramUser -> !Objects.equals(paramUser.getId(), user.getId()))
                .findAny()
                .orElse(null);

            int period =
                (int) ChronoUnit.DAYS.between(history.getStartDate(), history.getEndDate()) + 1;

            String noteTitle = "일기가 완성됐어요!";
            //String noteBody = diary.getTitle() + " 일기가 완성됐어요.";
            String noteBody =
                diary.getTitle() + "일기의 " + period + "일 일기가 완성됐어요! " + otherUser.getNickname()
                    + "님의 일기를 보러 가볼까요?";

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

            BatchResponse batchResponse = fcmService.sendNotification(
                createNote(noteTitle, noteBody, noteData),
                tokens);

            log.info("finishHistory push fcm successCount: {}", batchResponse.getSuccessCount());
            log.info("finishHistory push fcm failCount: {}", batchResponse.getFailureCount());
            log.info("finishHistory push fcm messageId {}",
                batchResponse.getResponses().stream()
                    .peek(response -> {
                        log.info("messageId: {}", response.getMessageId());
                    })
            );
        }

    }

    public void remindHistoryOne(History history) throws FirebaseMessagingException {
        log.info("remindHistoryOne call");
        remindHistory(history, "3");
    }

    public void remindHistoryTwo(History history) throws FirebaseMessagingException {
        log.info("remindHistoryTwo call");
        remindHistory(history, "1");
    }

    private void remindHistory(History history, String remainingDays)
        throws FirebaseMessagingException {
        Diary diary = history.getDiary();
        String title = diary.getTitle();
        List<User> users = userRepository.findUsers(history);
        log.info("remainingDays: {}", remainingDays);

        Map<User, Set<String>> fcmTokens = getFcmTokens(users);
        log.info("remindHistory of fcmTokens: {}", fcmTokens);

        for (User user : fcmTokens.keySet()) {
            Set<String> tokens = fcmTokens.get(user);
            if (tokens.isEmpty()) {
                return;
            }

            User otherUser = users.stream()
                .filter(paramUser -> !Objects.equals(paramUser.getId(), user.getId()))
                .findAny()
                .orElse(null);

            String noteTitle =
                remainingDays + "일 후 " + title + " 에서 " + otherUser.getNickname()
                    + " 님이 작성한 일기가 도착해요 !";
            String noteBody = "";

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

            BatchResponse batchResponse = fcmService.sendNotification(
                createNote(noteTitle, noteBody, noteData),
                tokens);

            log.info("remindHistory push fcm successCount: {}", batchResponse.getSuccessCount());
            log.info("remindHistory push fcm failCount: {}", batchResponse.getFailureCount());
            log.info("remindHistory push fcm messageId {}",
                batchResponse.getResponses().stream()
                    .peek(response -> {
                        log.info("messageId: {}", response.getMessageId());
                    })
            );
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
            if (user.getPushEnabled()) {
                fcmTokens.put(user, user.getFcmTokens());
            }
        }
        return fcmTokens;
    }

}
