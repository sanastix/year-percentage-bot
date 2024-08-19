package com.example.year_percentage_bot;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class BotUser {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long chatId;
    private boolean botRunning;

    public BotUser() {}

    public BotUser(Long chatId, boolean botRunning) {
        this.chatId = chatId;
        this.botRunning = botRunning;
    }

}
