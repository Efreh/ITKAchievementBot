package com.efr.ITKAchievementBot.bot.strategy;

import com.efr.ITKAchievementBot.model.AchievementDefinition;
import com.efr.ITKAchievementBot.model.UserDB;
import org.telegram.telegrambots.meta.api.objects.Message;

public interface AchievementStrategy {
    boolean isSatisfied(UserDB user, AchievementDefinition definition, Message message);
}