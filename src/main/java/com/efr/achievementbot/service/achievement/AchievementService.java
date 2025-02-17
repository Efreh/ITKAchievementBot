package com.efr.achievementbot.service.achievement;

import com.efr.achievementbot.bot.ITKAchievementBot;
import com.efr.achievementbot.model.Achievement;
import com.efr.achievementbot.model.AchievementDefinition;
import com.efr.achievementbot.model.UserDB;
import com.efr.achievementbot.repository.achievement.AchievementDefinitionRepository;
import com.efr.achievementbot.repository.achievement.AchievementRepository;
import com.efr.achievementbot.repository.user.UserRepository;
import com.efr.achievementbot.service.achievement.image.AchievementImageGenerator;
import com.efr.achievementbot.service.achievement.notification.AchievementNotificationService;
import com.efr.achievementbot.service.achievement.strategy.AchievementStrategy;
import com.efr.achievementbot.service.achievement.strategy.AchievementStrategyFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Message;

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
    // Компонент для отправки уведомлений
    private final AchievementNotificationService notificationService;

    /**
     * Проверка достижений для пользователя.
     *
     * @param user    пользователь из БД
     * @param message сообщение из Telegram
     * @param bot     экземпляр Telegram-бота
     */
    public void checkAchievements(UserDB user, Message message, ITKAchievementBot bot) {
        log.info("Проверка достижений для пользователя {} (telegramId: {})", user.getUserName(), user.getTelegramId());
        // Получаем достижения пользователя
        List<Achievement> userAchievements = achievementRepository.findByUserWithDefinition(user);
        // Загружаем все определения достижений
        List<AchievementDefinition> definitions = definitionRepository.findAll();

        for (AchievementDefinition definition : definitions) {
            // Пропускаем, если достижение уже получено
            boolean hasAchievement = userAchievements.stream()
                    .anyMatch(a -> a.getDefinition().getId().equals(definition.getId()));
            if (hasAchievement) {
                continue;
            }

            // Получаем стратегию для проверки достижения по типу
            AchievementStrategy strategy = strategyFactory.getStrategy(definition.getType());
            if (strategy == null) {
                continue;
            }

            // Если условие выполнено – выдаем достижение
            if (strategy.isSatisfied(user, definition, message)) {
                awardBaseAchievement(user, definition, message, bot);
            }
        }
    }

    /**
     * Выдача стандартного достижения.
     *
     * @param user       пользователь
     * @param definition определение достижения
     * @param message    сообщение из Telegram
     * @param bot        экземпляр Telegram-бота
     */
    private void awardBaseAchievement(UserDB user, AchievementDefinition definition, Message message, ITKAchievementBot bot) {
        log.info("Выдача достижения '{}' пользователю '{}'", definition.getTitle(), user.getUserName());
        // Генерируем изображение достижения
        File imageFile = imageGenerator.createAchievementImage(definition.getTitle(), definition.getDescription());
        // Асинхронная отправка уведомления
        notificationService.sendAchievementNotification(user, message, bot, imageFile, definition.getTitle());

        // Сохраняем выданное достижение в БД
        Achievement achievement = new Achievement();
        achievement.setUser(user);
        achievement.setDefinition(definition);
        achievement.setAwardedAt(LocalDateTime.now());
        achievementRepository.save(achievement);
        log.info("Достижение '{}' успешно выдано пользователю '{}'", definition.getTitle(), user.getUserName());
    }

    /**
     * Выдача кастомного достижения.
     *
     * @param userTag     тег пользователя (формат @username)
     * @param title       заголовок достижения
     * @param description описание достижения
     * @param chatId      ID чата, где требуется выдача
     * @param bot         экземпляр Telegram-бота
     */
    public void awardCustomAchievement(String userTag, String title, String description, Long chatId, ITKAchievementBot bot) {
        log.info("Выдача кастомного достижения '{}' для пользователя с тегом '{}'", title, userTag);
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

        // Генерация изображения достижения
        try {
            File image = imageGenerator.createAchievementImage(title, description);
            notificationService.sendCustomAchievementNotification(userTag, chatId, bot, image, title);
        } catch (Exception e) {
            log.error("Ошибка при выдаче кастомного достижения '{}'", title, e);
        }
        log.info("Кастомное достижение '{}' успешно выдано пользователю с тегом '{}'", title, userTag);
    }
}