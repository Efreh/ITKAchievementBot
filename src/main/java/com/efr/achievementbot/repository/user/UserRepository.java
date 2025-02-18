package com.efr.achievementbot.repository.user;

import com.efr.achievementbot.model.UserDB;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<UserDB, Long> {
    UserDB findByTelegramId(Long telegramId);
    UserDB findByTelegramIdAndChatId(Long telegramId, Long chatId);
    UserDB findByUserTagAndChatId(String userTag, Long chatId);

    // Метод для поиска пользователя с максимальным количеством сообщений за неделю
    UserDB findTopByOrderByWeeklyMessageCountDesc();
}
