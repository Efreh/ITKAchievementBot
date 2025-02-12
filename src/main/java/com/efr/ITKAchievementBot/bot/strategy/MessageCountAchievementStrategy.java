package com.efr.ITKAchievementBot.bot.strategy;

import com.efr.ITKAchievementBot.model.AchievementDefinition;
import com.efr.ITKAchievementBot.model.UserDB;
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
