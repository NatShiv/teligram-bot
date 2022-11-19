package com.example.telegrambot.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import javax.persistence.*;
import java.time.LocalDateTime;

@Entity(name = "Notification")
@Data
@NoArgsConstructor
public class NotificationMessage {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    String messageNotification;
    LocalDateTime dataNotification;
         Long userChatId;

    public NotificationMessage(LocalDateTime data, String text,  Long userChatId) {
        this.dataNotification=data;
        this.messageNotification=text;
        this.userChatId=userChatId;
    }
}
