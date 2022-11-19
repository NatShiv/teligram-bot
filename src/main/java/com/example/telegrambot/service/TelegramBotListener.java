package com.example.telegrambot.service;


import com.example.telegrambot.configuration.TelegramBotConfiguration;
import com.example.telegrambot.entity.NotificationMessage;
import com.example.telegrambot.repositiry.NotificationMessageRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.MessageEntity;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;

@Component
@Slf4j
public class TelegramBotListener extends TelegramLongPollingBot {

    LocalDateTime dateTime = LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES);
    private final TelegramBotConfiguration configuration;
    private final NotificationMessageRepository notificationMessageRepository;

    public TelegramBotListener(TelegramBotConfiguration configuration, NotificationMessageRepository notificationMessageRepository) {
        this.configuration = configuration;
        this.notificationMessageRepository = notificationMessageRepository;
    }


    @Override
    public String getBotUsername() {

        return configuration.getBotName();
    }

    @Override
    public String getBotToken() {
        return configuration.getToken();
    }

    @Override
    public void onUpdateReceived(Update update) {
        log.debug("вызван блок для получения всех входящих сообщений");
        if (update.hasMessage()) {
            Message message = update.getMessage();
            if (message.hasText() && message.hasEntities()) {
                handleMessage(message);
            }
            handleText(message);
        }
    }

    private void handleMessage(Message message) {
        log.debug("вызваа блок для извлечения из сообщения и обработки команды");
        long chatId = message.getChatId();
        if (message.hasText() && message.hasEntities()) {
            Optional<MessageEntity> commandEntity = message.getEntities().stream().filter(e -> "bot_command".equals(e.getType())).findFirst();
            if (commandEntity.isPresent()) {
                String command = message.getText().substring(commandEntity.get().getOffset(), commandEntity.get().getLength());
                switch (command) {
                    case "/start":
                        log.debug("вызваа команда /start");
                        startCommand(chatId, message.getFrom().getFirstName());
                        break;
                    case "/myData":
                        log.debug("вызвана команда /myData");
                        sendMessage(chatId, "Покажет ваши напоминания, если они есть");
                        getData(chatId);
                        break;
                    default:
                        sendMessage(chatId, "Извините, данная команда пока не поддерживается.");
                }
            }
        }
    }

    public void getData(long chatId) {
        log.debug("вызван блок для отправки всех имеющихся у пользователя запланированныз задач");
        List<NotificationMessage> messages = new ArrayList<>(notificationMessageRepository.findAllByUserChatId(chatId));
        if (!messages.isEmpty()) {
            for (NotificationMessage n : messages) {
                sendMessage(n.getUserChatId(), n.getMessageNotification());
            }
        }
    }

    public void startCommand(Long chatId, String name) {
        log.debug("вызван приветственный блок кода после команды старт");
        String answer = "Привет, " + name + " приятно познакомится!";
        sendMessage(chatId, answer);
    }

    public void sendMessage(Long chatId, String messageToSend) {
        log.debug("вызван блок для создания исходящего сообщения");
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(String.valueOf(chatId));
        sendMessage.setText(messageToSend);
        try {
            execute(sendMessage);
        } catch (TelegramApiException e) {
            log.error("Возникла ошибка при отправке сообщения в телеграм");
        }
    }

    void handleText(Message message) {
        log.debug("вызван блок обработки сообщений не содержащих команд бота");
        String text = message.getText();
        Long chatId = message.getChatId();

        Pattern patternForNotification = Pattern.compile("([0-9\\.\\:\\s]{16})(\\s)([\\W+]+)");
        if (patternForNotification.matcher(text).find()) {
            log.debug("вызван блок когда сообщение соответствует паттерну задачи для перехвата");
            LocalDateTime data = LocalDateTime.parse(text.substring(0, 16), DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm")).truncatedTo(ChronoUnit.MINUTES);
            if (data.compareTo(LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES)) <= 0) {
                log.debug("вызван блок когда переданное время задачи меньше текущего");
                sendMessage(chatId, "Время напоминания не может быть меньше текущего");
                return;
            }
            String notification = text.substring(16 + 1);

            if (notificationMessageRepository.findByDataAndNotification(data, notification).isPresent()) {
                sendMessage(chatId, "Такое напоминание уже существует");
            } else {
                log.debug("вызван блок сохранения новой ззадачи в базу");
                notificationMessageRepository.save(new NotificationMessage(data, text, chatId));
            }
            if (data.compareTo(dateTime) < 0) {
                log.debug("вызван блок изменения даты для отбора сообщений для метода по расписанию");
                dateTime = data;
            }

        }
    }


    @Scheduled(cron = " 0 0/1 * * * *")
    public void sendNotification() {
        log.debug("Вызван метод по расписанию. Проверяюший время напоминаний в базе.");
        while (dateTime.compareTo(LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES)) <= 0) {

            List<NotificationMessage> messages = new ArrayList<>(notificationMessageRepository.findAllByDataNotification(dateTime));

            if (!messages.isEmpty()) {
                log.debug("Вызван блок кода для передачи сообщений выбранных из базы");
                for (NotificationMessage n : messages) {
                    sendMessage(n.getUserChatId(), n.getMessageNotification());
                    notificationMessageRepository.delete(n);
                }
            }
            PageRequest pageRequest = PageRequest.of(0, 1);
            dateTime = notificationMessageRepository.findMinData(pageRequest).orElseGet(() -> LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES).plusDays(1));
        }
    }
}




