package com.example.year_percentage_bot.service;

import com.example.year_percentage_bot.BotUser;
import com.example.year_percentage_bot.BotUserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Slf4j
@Service
public class ScheduledTaskService {

    private final BotUserRepository botUserRepository;
    private final MessageSenderService messageSenderService;

    public ScheduledTaskService(BotUserRepository botUserRepository, MessageSenderService messageSenderService) {
        this.botUserRepository = botUserRepository;
        this.messageSenderService = messageSenderService;
    }

    @Scheduled(cron = "0 0 9 * * ?")
    public void sendDailyMessage() {
        log.info("Scheduled task started");
        List<BotUser> botUsers = botUserRepository.findByBotRunningTrue();
        long daysPassed = ChronoUnit.DAYS.between(LocalDate.of(LocalDate.now().getYear(), 1, 1), LocalDate.now());
        long totalDays = LocalDate.now().isLeapYear() ? 366 : 365;
        double percentage = (double) daysPassed / totalDays * 100;

        for (BotUser botUser : botUsers) {
            String messageText = String.format("%.2f%% of the year has already passed", percentage);
            messageSenderService.sendMessage(botUser.getChatId(), messageText);
        }
    }
}
