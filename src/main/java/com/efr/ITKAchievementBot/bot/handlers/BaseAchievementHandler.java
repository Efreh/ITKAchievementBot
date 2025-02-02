package com.efr.ITKAchievementBot.bot.handlers;

import com.efr.ITKAchievementBot.bot.ITKAchievementBot;
import com.efr.ITKAchievementBot.bot.service.AchievementImageGenerator;
import com.efr.ITKAchievementBot.model.UserDB;
import lombok.RequiredArgsConstructor;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.File;

@RequiredArgsConstructor
public abstract class BaseAchievementHandler implements AchievementHandler {
    private final AchievementImageGenerator imageGenerator;
    protected final String achievementName;
    protected final String achievementTitle;
    protected final String achievementDescription;

    @Override
    public void handle(UserDB user, Message message, ITKAchievementBot bot) throws TelegramApiException {
        if (shouldReceiveAchievement(user)) {
            File imageFile = imageGenerator.createAchievementImage(
                    achievementTitle,
                    achievementDescription
            );

            sendAchievement(user, message, bot, imageFile);
            user.getAchievements().add(achievementName);
        }
    }

    private void sendAchievement(UserDB user, Message message, ITKAchievementBot bot, File imageFile)
            throws TelegramApiException {

        SendPhoto sendPhoto = new SendPhoto();
        sendPhoto.setChatId(user.getChatId().toString());
        sendPhoto.setMessageThreadId(message.getMessageThreadId());
        sendPhoto.setCaption(user.getUserName() + " получает достижение!");

        try {
            sendPhoto.setPhoto(new InputFile(imageFile));
            bot.execute(sendPhoto);
        } finally {
            imageFile.delete(); // Удаляем временный файл
        }
    }

    protected abstract boolean shouldReceiveAchievement(UserDB user);
}