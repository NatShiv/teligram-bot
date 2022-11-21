package com.example.telegrambot.repositiry;

import com.example.telegrambot.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {
    @Query("SELECT n FROM notification n where n.dataNotification = :dateTime and n.messageNotification=:text and n.userChatId=:userChatId")
    Optional<Notification> findByDataAndNotification(Long userChatId, LocalDateTime dateTime, String text);

    List<Notification> findAllByDataNotification(LocalDateTime dateTime);

    @Query(value = "SELECT n.data_notification FROM notification n order by n.data_notification Limit 1 ", nativeQuery = true)
    Optional<LocalDateTime> findMinData();

    List<Notification> findAllByUserChatId(long chatId);
}
