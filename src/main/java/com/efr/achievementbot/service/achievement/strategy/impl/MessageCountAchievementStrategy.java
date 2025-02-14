package com.efr.achievementbot.service.achievement.strategy.impl;

import com.efr.achievementbot.service.achievement.strategy.AchievementStrategy;
import com.efr.achievementbot.model.AchievementDefinition;
import com.efr.achievementbot.model.UserDB;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Message;

@Component
public class MessageCountAchievementStrategy implements AchievementStrategy {
    @Override
    public boolean isSatisfied(UserDB user, AchievementDefinition definition, Message message) {
        // Проверяем, что количество сообщений пользователя больше или равно требуемому значению
        return user.getMessageCount() >= definition.getRequiredValue();
    }
}
