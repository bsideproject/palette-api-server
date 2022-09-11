package com.palette.infra.fcm;

import com.google.firebase.messaging.BatchResponse;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.MulticastMessage;
import com.google.firebase.messaging.Notification;
import com.palette.user.domain.User;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class FcmService {

    private final FirebaseMessaging firebaseMessaging;

    public BatchResponse sendNotification(Note note, Set<String> tokens)
        throws FirebaseMessagingException {
        Notification notification = buildNotification(note);

        MulticastMessage multicastMessage = MulticastMessage
            .builder()
            .addAllTokens(tokens)
            .setNotification(notification)
            .putAllData(note.getData())
            .build();
        return firebaseMessaging.sendMulticast(multicastMessage);
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

    public Set<String> getTokens(List<User> users) {
        if (users.isEmpty()) {
            return new HashSet<>();
        }
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
