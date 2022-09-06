package com.palette.infra.fcm;

import com.google.firebase.messaging.*;
import com.palette.user.domain.User;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Component
@AllArgsConstructor
public class FcmService {

    private final FirebaseMessaging firebaseMessaging;

    public String sendNotification(Note note, Set<String> tokens) throws FirebaseMessagingException {
        Notification notification = buildNotification(note);

        MulticastMessage multicastMessage = MulticastMessage
                .builder()
                .addAllTokens(tokens)
                .setNotification(notification)
                .putAllData(note.getData())
                .build();
        return firebaseMessaging.sendMulticast(multicastMessage).toString();
    }

    public String sendNotification(Note note, String token) throws FirebaseMessagingException {
        Notification notification = buildNotification(note);

        Message message = Message
                .builder()
                .setToken(token)
                .setNotification(notification)
                .putAllData(note.getData())
                .build();

        return firebaseMessaging.send(message);
    }

    public HashSet<String> getTokens(List<User> users) {
        if (users.isEmpty()) return new HashSet<>();
        HashSet<String> tokens = new HashSet<>();
        users.forEach(user -> {
            tokens.addAll(user.getFcmTokens());
        });
        return tokens;
    }

    private Notification buildNotification(Note note) {
        return Notification
                .builder()
                .setTitle(note.getTitle())
                .setBody(note.getBody())
                .build();
    }
}
