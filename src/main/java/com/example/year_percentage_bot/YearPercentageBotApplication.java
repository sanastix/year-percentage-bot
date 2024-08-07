package com.example.year_percentage_bot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

@SpringBootApplication
@EnableScheduling
@EnableConfigurationProperties(BotConfig.class)
@ConfigurationPropertiesScan
public class YearPercentageBotApplication {

	public static void main(String[] args) {
		SpringApplication.run(YearPercentageBotApplication.class, args);
	}

	@Bean
	public TelegramBotsApi telegramBotsApi(YearPercentageBot bot) throws TelegramApiException {
		TelegramBotsApi telegramBotsApi = new TelegramBotsApi(DefaultBotSession.class);
		telegramBotsApi.registerBot(bot);
		return telegramBotsApi;
	}
}
