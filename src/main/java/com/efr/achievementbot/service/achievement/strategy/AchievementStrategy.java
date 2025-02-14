package com.efr.achievementbot.service.achievement.strategy;

import com.efr.achievementbot.model.AchievementDefinition;
import com.efr.achievementbot.model.UserDB;
import org.telegram.telegrambots.meta.api.objects.Message;

public interface AchievementStrategy {
    boolean isSatisfied(UserDB user, AchievementDefinition definition, Message message);
}