package com.efr.achievementbot.scheduler;

import com.efr.achievementbot.bot.ITKAchievementBot;
import com.efr.achievementbot.model.UserDB;
import com.efr.achievementbot.repository.user.UserRepository;
import com.efr.achievementbot.service.achievement.AchievementService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class WeeklyActivityScheduler {

    private final UserRepository userRepository;
    private final AchievementService achievementService;
    private final ITKAchievementBot bot;

    /**
     * Запускается каждое воскресенье в 00:00 (по серверному времени).
     * Можно скорректировать cron-выражение под ваши нужды.
     */
    @Scheduled(cron = "0 0 0 ? * SUN")
//    @Scheduled(fixedDelay = 30000) // Период - 0.5 минута, для проверки

    public void awardWeeklyActivityAchievement() {
        log.info("Начало проверки еженедельной активности пользователей.");

        // Находим пользователя с максимальным количеством сообщений за неделю
        UserDB topUser = userRepository.findTopByOrderByWeeklyMessageCountDesc();

        if (topUser != null && topUser.getWeeklyMessageCount() > 0) {
            log.info("Пользователь {} (telegramId: {}) лидирует с {} сообщениями за неделю.",
                    topUser.getUserName(), topUser.getTelegramId(), topUser.getWeeklyMessageCount());

            // Выдаем кастомное достижение "Лучший активный пользователь недели"
            // Параметры: userTag, title, description, chatId, bot
            achievementService.awardCustomAchievement(
                    topUser.getUserTag(),
                    "Лорд виртуальных вестей",
                    "Твои слова ведут за собой, словно сигналы в цифровом эфире. Ты заслужил титул Лорда виртуальных вестей за свою активность.",
                    topUser.getChatId(),
                    bot
            );
        } else {
            log.info("За прошедшую неделю не было активности для награждения.");
        }

        // Сброс еженедельного счетчика для всех пользователей
        resetWeeklyMessageCounts();
    }

    /**
     * Сбрасывает счетчик еженедельной активности для всех пользователей.
     */
    private void resetWeeklyMessageCounts() {
        List<UserDB> users = userRepository.findAll();
        for (UserDB user : users) {
            user.setWeeklyMessageCount(0);
        }
        userRepository.saveAll(users);
        log.info("Еженедельные счетчики активности пользователей сброшены.");
    }
}
