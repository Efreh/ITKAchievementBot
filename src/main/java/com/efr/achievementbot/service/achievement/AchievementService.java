package com.efr.achievementbot.service.achievement;

import com.efr.achievementbot.bot.ITKAchievementBot;
import com.efr.achievementbot.bot.service.AchievementImageGenerator;
import com.efr.achievementbot.service.achievement.strategy.AchievementStrategy;
import com.efr.achievementbot.service.achievement.strategy.AchievementStrategyFactory;
import com.efr.achievementbot.model.Achievement;
import com.efr.achievementbot.model.AchievementDefinition;
import com.efr.achievementbot.model.UserDB;
import com.efr.achievementbot.repository.achievement.AchievementDefinitionRepository;
import com.efr.achievementbot.repository.achievement.AchievementRepository;
import com.efr.achievementbot.repository.user.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.File;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class AchievementService {

    private final AchievementDefinitionRepository definitionRepository;
    private final AchievementRepository achievementRepository;
    private final AchievementStrategyFactory strategyFactory;
    private final AchievementImageGenerator imageGenerator;
    private final UserRepository userRepository;

    public void checkAchievements(UserDB user, Message message, ITKAchievementBot bot) {
        // Загружаем все достижения пользователя одним запросом
        List<Achievement> userAchievements = achievementRepository.findByUserWithDefinition(user);

        // Получаем все определения достижений
        List<AchievementDefinition> definitions = definitionRepository.findAll();

        for (AchievementDefinition definition : definitions) {
            // Проверяем, есть ли у пользователя это достижение
            boolean hasAchievement = userAchievements.stream()
                    .anyMatch(a -> a.getDefinition().getId().equals(definition.getId()));

            if (hasAchievement) {
                continue; // Пропускаем, если достижение уже есть
            }

            // Получаем стратегию для данного типа достижения
            AchievementStrategy strategy = strategyFactory.getStrategy(definition.getType());
            if (strategy == null) {
                continue; // Пропускаем, если стратегия не найдена
            }

            // Проверяем, выполнено ли условие достижения
            if (strategy.isSatisfied(user, definition, message)) {
                awardBaseAchievement(user, definition, message, bot);
            }
        }
    }

    private void awardBaseAchievement(UserDB user, AchievementDefinition definition, Message message, ITKAchievementBot bot) {
        // Генерируем изображение достижения
        File imageFile = imageGenerator.createAchievementImage(definition.getTitle(), definition.getDescription());

        try {
            sendAchievementNotification(user, message, bot, imageFile, definition.getTitle());
        } catch (TelegramApiException e) {
            log.error("Ошибка при отправке уведомления о достижении", e);
        }

        // Сохраняем выданное достижение в БД
        Achievement achievement = new Achievement();
        achievement.setUser(user);
        achievement.setDefinition(definition);
        achievement.setAwardedAt(LocalDateTime.now());
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

    public void awardCustomAchievement(String userTag, String title, String description, Long chatId, ITKAchievementBot bot) {
        UserDB user = userRepository.findByUserTagAndChatId(userTag, chatId);

        if (user == null) {
            user = new UserDB();
            user.setUserTag(userTag);
            user.setChatId(chatId);
            user = userRepository.save(user);
        }

        AchievementDefinition definition = new AchievementDefinition();
        definition.setTitle(title);
        definition.setDescription(description);
        definition.setType("custom");
        definition = definitionRepository.save(definition);

        Achievement achievement = new Achievement();
        achievement.setUser(user);
        achievement.setDefinition(definition);
        achievement.setAwardedAt(LocalDateTime.now());
        achievementRepository.save(achievement);

        try {
            File image = imageGenerator.createAchievementImage(title, description);
            sendCustomAchievementNotification(userTag, chatId, bot, image, title);
        } catch (Exception e) {
            log.error("Error generating achievement image", e);
        }
    }

    private void sendCustomAchievementNotification(String userTag, Long chatId, ITKAchievementBot bot, File image, String title) {
        SendPhoto photo = new SendPhoto();
        photo.setChatId(chatId.toString());
        photo.setCaption(userTag + " получает достижение: " + title + "!");

        try {
            photo.setPhoto(new InputFile(image));
            bot.execute(photo);
        } catch (TelegramApiException e) {
            log.error("Ошибка отправки достижения", e);
        } finally {
            image.delete();
        }
    }
}