package com.efr.achievementbot.service.achievement.strategy.impl;

import com.efr.achievementbot.service.achievement.strategy.AchievementStrategy;
import com.efr.achievementbot.model.AchievementDefinition;
import com.efr.achievementbot.model.UserDB;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Message;

@Component
public class KeywordAchievementStrategy implements AchievementStrategy {
    @Override
    public boolean isSatisfied(UserDB user, AchievementDefinition definition, Message message) {
        if (message == null || !message.hasText()) return false;

        String requiredKeyword = definition.getRequiredKeyword();
        return requiredKeyword != null &&
                message.getText().toLowerCase().contains(requiredKeyword.toLowerCase());
    }
}