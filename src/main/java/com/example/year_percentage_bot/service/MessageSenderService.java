package com.example.year_percentage_bot.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Slf4j
@Service
public class MessageSenderService {

    private final TelegramLongPollingBot telegramBot;

    public MessageSenderService(TelegramLongPollingBot telegramBot) {
        this.telegramBot = telegramBot;
    }

    public void sendMessage(Long chatId, String text) {
        SendMessage message = new SendMessage(String.valueOf(chatId), text);
        try {
            telegramBot.execute(message);
            log.info("Message sent successfully to chatId: {}", chatId);
        } catch (TelegramApiException e) {
            log.error("Failed to send message to chatId: {}", chatId, e);
        }
    }
}
