package com.efr.ITKAchievementBot.service;

import com.efr.ITKAchievementBot.bot.ITKAchievementBot;
import com.efr.ITKAchievementBot.bot.handlers.AchievementHandler;
import com.efr.ITKAchievementBot.model.UserDB;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AchievementService {
    private final List<AchievementHandler> handlers;

    public void checkAchievements(UserDB user, Message message, ITKAchievementBot bot) {
        handlers.forEach(handler -> {
            try {
                handler.handle(user, message, bot);
            } catch (TelegramApiException e) {
                // Обработка ошибок
            }
        });
    }
}