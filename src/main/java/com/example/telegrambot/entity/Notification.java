package com.example.telegrambot.entity;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity(name = "notification")
@Data
@NoArgsConstructor
public class Notification {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    String messageNotification;
    LocalDateTime dataNotification;
    Long userChatId;

    public Notification(LocalDateTime data, String text, Long userChatId) {
        this.dataNotification = data;
        this.messageNotification = text;
        this.userChatId = userChatId;
    }
}
