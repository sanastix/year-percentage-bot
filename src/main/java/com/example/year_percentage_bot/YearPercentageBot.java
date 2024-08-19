package com.example.year_percentage_bot;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Component
public class YearPercentageBot extends TelegramLongPollingBot {

    private static final Logger logger = LoggerFactory.getLogger(YearPercentageBot.class);
    private static final String START_BOT_REQUEST = "Start this bot";
    private static final String STOP_BOT_REQUEST = "Stop this bot";
    //private static boolean botRunning = false;
    //private static Long chatId = null;
    private final BotConfig config;
    private final BotUserRepository botUserRepository;

    @Autowired
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
                logger.error("Failed to send response message to chatId: {}", message.getChatId(), e);
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

    @Scheduled(cron = "* * * * * ?")
    //@Scheduled(cron = "0 0 9 * * ?")
    public void sendDailyMessage() {
        logger.info("Scheduled task started");
        List<BotUser> botUsers = botUserRepository.findAll();
        for (BotUser botUser : botUsers) {
            if (botUser.isBotRunning()) {
                long daysPassed = ChronoUnit.DAYS.between(LocalDate.of(LocalDate.now().getYear(), 1, 1), LocalDate.now());
                long totalDays = LocalDate.now().isLeapYear() ? 366 : 365;
                double percentage = (double) daysPassed / totalDays * 100;
                String messageText = String.format("%.2f%% of the year has already passed", percentage);
                SendMessage message = new SendMessage(String.valueOf(botUser.getChatId()), messageText);
                try {
                    execute(message);
                    logger.info("Message sent successfully to chatId: " + botUser.getChatId());
                } catch (TelegramApiException e) {
                    logger.error("Failed to send message", e);
                }
            }
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
        return config.getBotUsername();
    }

    public String getBotToken() {
        return config.getBotToken();
    }

}
