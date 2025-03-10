package com.efr.achievementbot.service.achievement.strategy.impl;

import com.efr.achievementbot.service.achievement.strategy.AchievementStrategy;
import com.efr.achievementbot.model.AchievementDefinition;
import com.efr.achievementbot.model.UserDB;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Message;

@Component
public class MediaStrategy implements AchievementStrategy {

    @Override
    public boolean isSatisfied(UserDB user, AchievementDefinition definition, Message message) {
        return user.getMediaCount() >= definition.getRequiredValue();
    }

    @Override
    public String getType() {
        return "media";
    }
}
