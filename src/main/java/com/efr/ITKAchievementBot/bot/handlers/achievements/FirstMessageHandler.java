package com.efr.ITKAchievementBot.bot.handlers.achievements;

import com.efr.ITKAchievementBot.bot.handlers.BaseAchievementHandler;
import com.efr.ITKAchievementBot.bot.service.AchievementImageGenerator;
import com.efr.ITKAchievementBot.model.UserDB;
import org.springframework.stereotype.Component;

@Component
class FirstMessageHandler extends BaseAchievementHandler {
    public FirstMessageHandler(AchievementImageGenerator imageGenerator) {
        super(imageGenerator,
                "first_message",
                "Глас имеющий",
                "Написал первое сообщение в чате!");
    }

    @Override
    protected boolean shouldReceiveAchievement(UserDB user) {
        return user.getMessageCount() == 1 &&
                !user.getAchievements().contains("first_message");
    }
}