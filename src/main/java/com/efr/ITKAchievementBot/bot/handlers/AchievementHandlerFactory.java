package com.efr.ITKAchievementBot.bot.handlers;

import com.efr.ITKAchievementBot.bot.ITKAchievementBot;
import com.efr.ITKAchievementBot.bot.handlers.achievements.AchievementConfig;
import com.efr.ITKAchievementBot.bot.handlers.achievements.AchievementDefinition;
import com.efr.ITKAchievementBot.bot.service.AchievementImageGenerator;
import com.efr.ITKAchievementBot.model.UserDB;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.File;
import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class AchievementHandlerFactory {
    private final AchievementImageGenerator imageGenerator;
    private final AchievementConfig achievementConfig;

    public List<AchievementHandler> createHandlers() {
        return achievementConfig.getAchievements().entrySet().stream()
                .map(entry -> createHandler(entry.getKey(), entry.getValue()))
                .collect(Collectors.toList());
    }

    private AchievementHandler createHandler(String achievementId, AchievementDefinition definition) {
        return new AchievementHandler() {
            @Override
            public void handle(UserDB user, Message message, ITKAchievementBot bot) throws TelegramApiException {
                if (shouldAward(user, definition)) {
                    // Логика выдачи достижения
                    File imageFile = imageGenerator.createAchievementImage(
                            definition.getTitle(),
                            definition.getDescription()
                    );

                    SendPhoto sendPhoto = new SendPhoto();
                    sendPhoto.setChatId(user.getChatId().toString());
                    sendPhoto.setCaption(user.getUserName() + " получает достижение!");
                    sendPhoto.setPhoto(new InputFile(imageFile));

                    bot.execute(sendPhoto);
                    user.getAchievements().add(achievementId);
                }
            }

            private boolean shouldAward(UserDB user, AchievementDefinition definition) {
                switch (definition.getTriggerType()) {
                    case MESSAGE_COUNT:
                        return user.getMessageCount() == definition.getRequiredCount();
                    case MEDIA_COUNT:
                        return user.getMediaCount() >= definition.getRequiredCount();
                    case LIKE_COUNT:
                        return user.getLikesReceived() >= definition.getRequiredCount();
                    default:
                        return false;
                }
            }
        };
    }
}