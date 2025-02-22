package com.efr.achievementbot.scheduler;

import com.efr.achievementbot.bot.JavaCodeBot;
import com.efr.achievementbot.config.bot.BotProperties;
import com.efr.achievementbot.model.UserDB;
import com.efr.achievementbot.repository.user.UserRepository;
import com.efr.achievementbot.service.achievement.AchievementService;
import com.efr.achievementbot.service.achievement.image.AchievementImageGenerator;
import com.efr.achievementbot.service.config.BotConfigService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class WeeklyActivityScheduler {

    private final UserRepository userRepository;
    private final AchievementService achievementService;
    private final JavaCodeBot bot;
    private final AchievementImageGenerator imageGenerator;
    private final BotConfigService botConfigService;

    /**
     * Каждую неделю (или для проверки каждые 30 секунд) собирает топ-5 активных пользователей за неделю
     * и отправляет дашборд в группу, если есть активность.
     */
    @Scheduled(cron = "0 0 0 ? * SUN")
//    @Scheduled(fixedDelay = 30000) // Для тестирования – каждые 30 секунд
    public void awardWeeklyActivityAchievement() {
        log.info("Начало формирования топ-5 активности за неделю.");

        List<UserDB> users = userRepository.findAll();

        // Вычисляем недельный рейтинг для каждого пользователя
        Map<UserDB, Integer> userScores = new HashMap<>();
        for (UserDB user : users) {
            int messageScore = user.getWeeklyMessageCount() != null ? user.getWeeklyMessageCount() : 0;
            int achievementScore = user.getWeeklyAchievementScore() != null ? user.getWeeklyAchievementScore() : 0;
            int totalScore = messageScore + achievementScore;
            userScores.put(user, totalScore);
        }

        // Сортируем пользователей по убыванию общего недельного счёта и выбираем топ-5
        List<UserDB> topUsers = userScores.entrySet().stream()
                .sorted(Map.Entry.<UserDB, Integer>comparingByValue().reversed())
                .map(Map.Entry::getKey)
                .limit(5)
                .collect(Collectors.toList());

        // Если активности нет (максимальный счёт равен 0) – не отправляем уведомление
        if (topUsers.isEmpty() || getWeeklyScore(topUsers.get(0)) == 0) {
            log.info("За прошедшую неделю нет активности для формирования дашборда.");
        } else {
            try {
                // Генерируем изображение дашборда (с текстом чёрным, см. AchievementImageGenerator)
                File dashboardImage = imageGenerator.createDashboardImage(topUsers);
                // Формируем текстовое уведомление с упоминанием по tag и счётом
                StringBuilder captionBuilder = new StringBuilder("Топ 5 заклинателей кода за неделю:\n");
                for (int i = 0; i < topUsers.size(); i++) {
                    UserDB user = topUsers.get(i);
                    captionBuilder.append(String.format("%d. %s - %d очков восхождения\n", i + 1, getMention(user), getWeeklyScore(user)));
                }
                String caption = captionBuilder.toString();

                SendPhoto sendPhoto = new SendPhoto();
                sendPhoto.setChatId(botConfigService.getConfig().getGroupId());
                sendPhoto.setCaption(caption);
                sendPhoto.setPhoto(new InputFile(dashboardImage));
                bot.execute(sendPhoto);
                log.info("Дашборд топ-5 активных пользователей успешно отправлен.");
            } catch (TelegramApiException e) {
                log.error("Ошибка при отправке дашборда: {}", e.getMessage(), e);
            } catch (Exception e) {
                log.error("Ошибка при генерации дашборда: {}", e.getMessage(), e);
            }
        }

        // Сброс недельных счётчиков активности
        resetWeeklyScores();
    }

    private int getWeeklyScore(UserDB user) {
        int messageScore = user.getWeeklyMessageCount() != null ? user.getWeeklyMessageCount() : 0;
        int achievementScore = user.getWeeklyAchievementScore() != null ? user.getWeeklyAchievementScore() : 0;
        return messageScore + achievementScore;
    }

    // Возвращает отображаемое имя: если задано userName, используем его, иначе userTag
    private String getDisplayName(UserDB user) {
        if (user.getUserName() != null && !user.getUserName().trim().isEmpty()) {
            return user.getUserName();
        } else if (user.getUserTag() != null && !user.getUserTag().trim().isEmpty()) {
            return user.getUserTag();
        } else {
            return "Unknown";
        }
    }

    // Для упоминания в тексте используем userTag, если он задан, иначе формируем его на основе отображаемого имени
    private String getMention(UserDB user) {
        if (user.getUserTag() != null && !user.getUserTag().trim().isEmpty()) {
            return user.getUserTag();
        } else {
            return "@" + getDisplayName(user);
        }
    }

    private void resetWeeklyScores() {
        List<UserDB> users = userRepository.findAll();
        for (UserDB user : users) {
            user.setWeeklyMessageCount(0);
            user.setWeeklyAchievementScore(0);
        }
        userRepository.saveAll(users);
        log.info("Еженедельные счётчики активности сброшены.");
    }
}
