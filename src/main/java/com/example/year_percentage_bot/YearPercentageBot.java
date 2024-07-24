package com.example.year_percentage_bot;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

@Component
public class YearPercentageBot extends TelegramLongPollingBot {

    private static final String START_BOT_REQUEST = "Start this bot";
    private static final String STOP_BOT_REQUEST = "Stop this bot";
    private static boolean botRunning = false;
    private static Long chatId = null;

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            Message message = update.getMessage();
            try {
                execute(getResponseMessage(message));
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
        }
    }

    private SendMessage getResponseMessage(Message message) {
        switch (message.getText()) {
            case "/start":
                return getStartCommandMessage(message);
            case START_BOT_REQUEST:
                botRunning = true;
                chatId = message.getChatId();
                return getStartBotCommandMessage(message);
            case STOP_BOT_REQUEST:
                botRunning = false;
                chatId = message.getChatId();
                return getStopBotCommandMessage(message);
            default:
                return new SendMessage(String.valueOf(message.getChatId()), ":)");
        }
    }

    private SendMessage getStartCommandMessage(Message message) {
        SendMessage response = new SendMessage();
        response.setText("This bot can send messages about how many days of the year " +
                "have already passed (in percentage), every morning at 09:00");
        response.setChatId(message.getChatId());
        response.setReplyMarkup(getMainMenu());
        return response;
    }

    private SendMessage getStopBotCommandMessage(Message message) {
        SendMessage response = new SendMessage();
        response.setText("Bot was stopped");
        response.setChatId(message.getChatId());
        return response;
    }

    private SendMessage getStartBotCommandMessage(Message message) {
        SendMessage response = new SendMessage();
        response.setText("Bot was started");
        response.setChatId(message.getChatId());
        return response;
    }

    @Scheduled(cron = "0 0 9 * * ?")
    public void sendDailyMessage() {
        if (!botRunning || chatId == null) return;
        long daysPassed = ChronoUnit.DAYS.between(LocalDate.of(LocalDate.now().getYear(), 1, 1), LocalDate.now());
        long totalDays = LocalDate.now().isLeapYear() ? 366 : 365;
        double percentage = (double) daysPassed / totalDays * 100;
        String messageText = String.format("%.2f%% of the year has already passed", percentage);
        SendMessage message = new SendMessage(String.valueOf(chatId), messageText);
        try {
            execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    private ReplyKeyboard getMainMenu() {
        ReplyKeyboardMarkup markup = new ReplyKeyboardMarkup();
        KeyboardRow row = new KeyboardRow();
        List<KeyboardRow> rows = new ArrayList<>();
        row.add(START_BOT_REQUEST);
        row.add(STOP_BOT_REQUEST);
        rows.add(row);
        markup.setKeyboard(rows);
        return markup;
    }

    @Override
    public String getBotUsername() {
        return "year_percentage_bot";
    }

    public String getBotToken() {
        return "7338831089:AAG6Sv2ATHbN8Sm-GVEeFyW0WH7GNQ97r_0";
    }

}
