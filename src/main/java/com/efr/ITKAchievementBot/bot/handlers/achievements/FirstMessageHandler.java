package com.efr.ITKAchievementBot.bot.handlers.achievements;

import com.efr.ITKAchievementBot.bot.handlers.BaseAchievementHandler;
import com.efr.ITKAchievementBot.model.UserDB;
import org.springframework.stereotype.Component;

@Component
class FirstMessageHandler extends BaseAchievementHandler {
    public FirstMessageHandler() {
        super("first_message",
                "images/first_message.jpg",
                "Первое сообщение в чате!");
    }

    @Override
    protected boolean shouldReceiveAchievement(UserDB user) {
        return user.getMessageCount() == 1 &&
                !user.getAchievements().contains("first_message");
    }
}