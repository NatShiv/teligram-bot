package com.example.telegrambot.repositiry;

import com.example.telegrambot.entity.NotificationMessage;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface NotificationMessageRepository extends JpaRepository <NotificationMessage, Long>{
@Query("SELECT n FROM Notification n where n.dataNotification = :dateTime and n.messageNotification=:text")
    Optional< NotificationMessage> findByDataAndNotification(LocalDateTime dateTime, String text);

List<NotificationMessage> findAllByDataNotification(LocalDateTime dateTime);
    @Query("SELECT n.dataNotification FROM Notification n")
    Optional< LocalDateTime> findMinData(PageRequest pageRequest);

    List<NotificationMessage> findAllByUserChatId(long chatId);
}
