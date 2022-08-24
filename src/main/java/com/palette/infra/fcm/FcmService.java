package com.palette.infra.fcm;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.messaging.*;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.List;

@Component
@AllArgsConstructor
public class FcmService {

    private final FirebaseMessaging firebaseMessaging;

    public String sendNotification(Note note, List<String> tokens) throws FirebaseMessagingException {
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

    private Notification buildNotification(Note note) {
        return Notification
                .builder()
                .setTitle(note.getTitle())
                .setBody(note.getBody())
                .build();
    }
}
