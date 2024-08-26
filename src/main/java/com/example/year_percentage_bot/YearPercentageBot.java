package com.example.year_percentage_bot;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@Component
public class YearPercentageBot extends TelegramLongPollingBot {

    private static final String START_BOT_REQUEST = "Start this bot";
    private static final String STOP_BOT_REQUEST = "Stop this bot";
    private final BotConfig config;
    private final BotUserRepository botUserRepository;

    public YearPercentageBot(BotConfig config, BotUserRepository botUserRepository) {
        this.config = config;
        this.botUserRepository = botUserRepository;
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            Message message = update.getMessage();
            try {
                execute(getResponseMessage(message));
            } catch (TelegramApiException e) {
                log.error("Failed to send response message to chatId: {}", message.getChatId(), e);
            }
        }
    }

    private SendMessage getResponseMessage(Message message) {
        Long chatId = message.getChatId();
        Optional<BotUser> botUserOptional = botUserRepository.findByChatId(chatId);
        BotUser botUser = botUserOptional.orElseGet(() -> new BotUser(chatId, false));

        switch (message.getText()) {
            case "/start":
                return getStartCommandMessage(message);
            case START_BOT_REQUEST:
                botUser.setBotRunning(true);
                botUserRepository.save(botUser);
                return getStartBotCommandMessage(message);
            case STOP_BOT_REQUEST:
                botUser.setBotRunning(false);
                botUserRepository.save(botUser);
                return getStopBotCommandMessage(message);
            default:
                return new SendMessage(String.valueOf(message.getChatId()), message.getText() + " :)");
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
        return config.getBotUsername();
    }

    public String getBotToken() {
        return config.getBotToken();
    }
}
