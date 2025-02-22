package com.efr.achievementbot.scheduler;

import com.efr.achievementbot.service.user.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserCleanupScheduler {

    private final UserService userService;
    /**
     * Запускается каждые 3 месяца:
     * - 1 января, 1 апреля, 1 июля, 1 октября, в 00:00
     *
     * CRON: сек/мин/час/деньМесяца/месяц/деньНедели
     * 0 0 0 1 1,4,7,10 ?
     */
    @Scheduled(cron = "0 0 0 1 1,4,7,10 ?")
    public void removeInactiveUsers() {
        log.info("Запуск очистки пользователей, неактивных более года.");

        // Граница: кто не заходил больше года
        LocalDateTime cutoff = LocalDateTime.now().minusYears(1);

        // Вызываем DELETE
        int deletedCount = userService.deleteInactiveBefore(cutoff);

        log.info("Очистка завершена. Удалено пользователей: {}", deletedCount);
    }
}