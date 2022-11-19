package com.example.telegrambot.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.exceptions.TelegramApiRequestException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

@Slf4j
@Component
public class TelegramBotInitializer {
    final int RECONNECT_PAUSE = 10000;

    @Autowired
    TelegramBotListener bot;

    @EventListener({ContextRefreshedEvent.class})
    public void init() throws TelegramApiException {
        log.debug("вызван метод создающий бота");
        TelegramBotsApi telegramBotsApi = new TelegramBotsApi(DefaultBotSession.class);
        try {
            telegramBotsApi.registerBot(bot);
            log.info("TelegramAPI запущен.");
        } catch (TelegramApiRequestException e) {
            log.error("Соединение неваозможно. Пауза " + RECONNECT_PAUSE / 1000 + "сек до новой попытки. Error: " + e.getMessage());
            try {  log.debug("вызван блок паузы между побытками создать бота");
                Thread.sleep(RECONNECT_PAUSE);
            } catch (InterruptedException e1) {
                log.error("возникла ошибка при отправлении/пробуждении потока на паузу");
                e1.printStackTrace();
                return;
            }
            init();
        }
    }
}
