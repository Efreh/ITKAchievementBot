package com.efr.ITKAchievementBot.repository;

import com.efr.ITKAchievementBot.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
    User findByChatId(Long chatId);
}
