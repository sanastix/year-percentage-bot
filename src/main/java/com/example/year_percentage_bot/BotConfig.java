package com.example.year_percentage_bot;

import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@ConfigurationProperties(prefix = "bot")
public class BotConfig {

    private final String botUsername;

    private final String botToken;

    public BotConfig(String botUsername, String botToken) {
        this.botUsername = botUsername;
        this.botToken = botToken;
    }

}
