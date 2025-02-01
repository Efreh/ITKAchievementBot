package com.efr.ITKAchievementBot.bot.handlers;

import com.efr.ITKAchievementBot.bot.ITKAchievementBot;
import com.efr.ITKAchievementBot.model.UserDB;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

public interface AchievementHandler {
    void handle(UserDB user, Message message, ITKAchievementBot bot) throws TelegramApiException;
}
