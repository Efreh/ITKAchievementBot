package com.efr.ITKAchievementBot.repository;

import com.efr.ITKAchievementBot.model.UserDB;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<UserDB, Long> {
    UserDB findByTelegramId(Long telegramId);
    UserDB findByTelegramIdAndChatId(Long telegramId, Long chatId);
}
