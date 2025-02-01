package com.efr.ITKAchievementBot.bot.handlers.achievements;

import com.efr.ITKAchievementBot.bot.handlers.BaseAchievementHandler;
import com.efr.ITKAchievementBot.model.UserDB;
import org.springframework.stereotype.Component;

@Component
class FifthMessageHandler extends BaseAchievementHandler {
    public FifthMessageHandler() {
        super("fifth_message",
                "images/fifth_message.jpg",
                "5 сообщений в чате!");
    }

    @Override
    protected boolean shouldReceiveAchievement(UserDB user) {
        return user.getMessageCount() == 5 &&
                !user.getAchievements().contains("fifth_message");
    }
}