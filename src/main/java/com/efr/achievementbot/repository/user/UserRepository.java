package com.efr.achievementbot.repository.user;

import com.efr.achievementbot.model.UserDB;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

public interface UserRepository extends JpaRepository<UserDB, Long> {

    UserDB findByTelegramId(Long telegramId);
    UserDB findByTelegramIdAndChatId(Long telegramId, Long chatId);
    UserDB findByUserTagAndChatId(String userTag, Long chatId);

    @Modifying
    @Transactional
    @Query("DELETE FROM users u WHERE u.lastActivity < :cutoff")
    int deleteInactiveBefore(LocalDateTime cutoff);

}