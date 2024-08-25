package com.example.year_percentage_bot;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface BotUserRepository extends JpaRepository<BotUser, Long> {

    Optional<BotUser> findByChatId(Long chatId);

    List<BotUser> findByBotRunningTrue();
}
