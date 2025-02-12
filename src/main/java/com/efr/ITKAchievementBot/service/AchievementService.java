package com.efr.ITKAchievementBot.service;

import com.efr.ITKAchievementBot.bot.ITKAchievementBot;
import com.efr.ITKAchievementBot.bot.service.AchievementImageGenerator;
import com.efr.ITKAchievementBot.bot.strategy.AchievementStrategy;
import com.efr.ITKAchievementBot.bot.strategy.AchievementStrategyFactory;
import com.efr.ITKAchievementBot.model.Achievement;
import com.efr.ITKAchievementBot.model.AchievementDefinition;
import com.efr.ITKAchievementBot.model.UserDB;
import com.efr.ITKAchievementBot.repository.AchievementDefinitionRepository;
import com.efr.ITKAchievementBot.repository.AchievementRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.File;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class AchievementService {

    private final AchievementDefinitionRepository definitionRepository;
    private final AchievementRepository achievementRepository;
    private final AchievementStrategyFactory strategyFactory;
    private final AchievementImageGenerator imageGenerator;

    public void checkAchievements(UserDB user, Message message, ITKAchievementBot bot) {
        // Получаем все определения достижений.
        // Если в будущем появится несколько типов – можно добавить фильтрацию.
        List<AchievementDefinition> definitions = definitionRepository.findAll();

        for (AchievementDefinition definition : definitions) {
            // Если пользователь уже получил это достижение, пропускаем
            if (achievementRepository.existsByUserAndName(user, definition.getName())) {
                continue;
            }

            // Получаем стратегию для данного типа достижения
            AchievementStrategy strategy = strategyFactory.getStrategy(definition.getType());
            if (strategy == null) {
                // Если для данного типа нет стратегии, пропускаем
                continue;
            }

            // В классе AchievementService
            if (strategy.isSatisfied(user, definition, message)) {
                awardAchievement(user, definition, message, bot);
            }
        }
    }

    private void awardAchievement(UserDB user, AchievementDefinition definition, Message message, ITKAchievementBot bot) {
        // Генерируем изображение достижения
        File imageFile = imageGenerator.createAchievementImage(definition.getTitle(), definition.getDescription());

        try {
            sendAchievementNotification(user, message, bot, imageFile, definition.getTitle());
        } catch (TelegramApiException e) {
            // Можно добавить логирование ошибки
            e.printStackTrace();
        }

        // Сохраняем выданное достижение в БД
        Achievement achievement = new Achievement();
        achievement.setName(definition.getName());
        achievement.setTitle(definition.getTitle());
        achievement.setDescription(definition.getDescription());
        achievement.setUser(user);
        achievementRepository.save(achievement);
    }

    private void sendAchievementNotification(UserDB user, Message message, ITKAchievementBot bot, File imageFile, String achievementTitle)
            throws TelegramApiException {
        SendPhoto sendPhoto = new SendPhoto();
        sendPhoto.setChatId(user.getChatId().toString());
        sendPhoto.setMessageThreadId(message.getMessageThreadId());
        sendPhoto.setCaption(user.getUserName() + " получает достижение: " + achievementTitle + "!");

        try {
            sendPhoto.setPhoto(new InputFile(imageFile));
            bot.execute(sendPhoto);
        } finally {
            // Удаляем временный файл
            imageFile.delete();
        }
    }
}
