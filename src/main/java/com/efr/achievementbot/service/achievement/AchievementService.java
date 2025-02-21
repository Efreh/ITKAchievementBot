package com.efr.achievementbot.service.achievement;

import com.efr.achievementbot.bot.JavaCodeBot;
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
    private final AchievementDefinitionService achievementDefinitionService;
    private final AchievementNotificationService notificationService;

    /**
     * Проверяет, соответствует ли активность пользователя условиям достижения.
     * Если условие выполнено, выдаёт достижение и начисляет очки.
     */
    public void checkAchievements(UserDB user, Message message, JavaCodeBot bot) {
        log.info("Проверка достижений для пользователя {} (telegramId: {})", user.getUserName(), user.getTelegramId());
        List<Achievement> userAchievements = achievementRepository.findByUserWithDefinition(user);
        List<AchievementDefinition> definitions = achievementDefinitionService.getAllDefinitions();

        for (AchievementDefinition definition : definitions) {
            boolean alreadyAwarded = userAchievements.stream()
                    .anyMatch(a -> a.getDefinition().getId().equals(definition.getId()));
            if (alreadyAwarded) continue;

            AchievementStrategy strategy = strategyFactory.getStrategy(definition.getType());
            if (strategy == null) continue;

            if (strategy.isSatisfied(user, definition, message)) {
                awardBaseAchievement(user, definition, message, bot);
            }
        }
    }

    /**
     * Выдаёт стандартное достижение и начисляет очки.
     */
    private void awardBaseAchievement(UserDB user, AchievementDefinition definition, Message message, JavaCodeBot bot) {
        log.info("Выдача достижения '{}' пользователю '{}'", definition.getTitle(), user.getUserName());
        File imageFile = imageGenerator.createAchievementImage(definition.getTitle(), definition.getDescription());
        notificationService.sendAchievementNotification(user, message, bot, imageFile, definition.getTitle());

        Achievement achievement = new Achievement();
        achievement.setUser(user);
        achievement.setDefinition(definition);
        achievement.setAwardedAt(LocalDateTime.now());
        achievementRepository.save(achievement);

        // Начисление очков за достижение
        int weight = definition.getWeight() != null ? definition.getWeight() : 1;
        addAchievementPoints(user, weight);
        log.info("Достижение '{}' успешно выдано пользователю '{}'", definition.getTitle(), user.getUserName());
    }

    /**
     * Выдаёт кастомное достижение для пользователя по указанному тегу.
     */
    public void awardCustomAchievement(String userTag, String title, String description, Long chatId, JavaCodeBot bot) {
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
        definition.setWeight(3);
        definition = definitionRepository.save(definition);

        Achievement achievement = new Achievement();
        achievement.setUser(user);
        achievement.setDefinition(definition);
        achievement.setAwardedAt(LocalDateTime.now());
        achievementRepository.save(achievement);

        addAchievementPoints(user, definition.getWeight());

        try {
            File image = imageGenerator.createAchievementImage(title, description);
            notificationService.sendCustomAchievementNotification(userTag, chatId, bot, image, title);
        } catch (Exception e) {
            log.error("Ошибка при выдаче кастомного достижения '{}'", title, e);
        }
        log.info("Кастомное достижение '{}' успешно выдано пользователю с тегом '{}'", title, userTag);
    }

    /**
     * Начисляет очки достижения пользователю.
     */
    private void addAchievementPoints(UserDB user, int points) {
        user.setAchievementScore((user.getAchievementScore() != null ? user.getAchievementScore() : 0) + points);
        user.setWeeklyAchievementScore((user.getWeeklyAchievementScore() != null ? user.getWeeklyAchievementScore() : 0) + points);
        user.setMonthlyAchievementScore((user.getMonthlyAchievementScore() != null ? user.getMonthlyAchievementScore() : 0) + points);
        userRepository.save(user);
    }
}