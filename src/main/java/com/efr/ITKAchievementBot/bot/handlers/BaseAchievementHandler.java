package com.efr.ITKAchievementBot.bot.handlers;

import com.efr.ITKAchievementBot.bot.ITKAchievementBot;
import com.efr.ITKAchievementBot.model.UserDB;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ClassPathResource;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.File;
import java.io.IOException;

@RequiredArgsConstructor
public abstract class BaseAchievementHandler implements AchievementHandler {
    protected final String achievementName;
    protected final String imagePath;
    protected final String achievementText;

    @Override
    public void handle(UserDB user, Message message, ITKAchievementBot bot) throws TelegramApiException {
        if (shouldReceiveAchievement(user)) {
            sendAchievement(user, message, bot);
            user.getAchievements().add(achievementName);
        }
    }

    protected abstract boolean shouldReceiveAchievement(UserDB user);

    private void sendAchievement(UserDB user, Message message, ITKAchievementBot bot) throws TelegramApiException {
        SendPhoto sendPhoto = new SendPhoto();
        sendPhoto.setChatId(user.getChatId().toString());
        sendPhoto.setCaption(user.getUserName() + " получает достижение: " + achievementText);

        try {
            File imageFile = new ClassPathResource(imagePath).getFile();
            sendPhoto.setPhoto(new InputFile(imageFile));
        } catch (IOException e) {
            throw new TelegramApiException("Error loading image", e);
        }

        bot.execute(sendPhoto);
    }
}