package com.efr.achievementbot.scheduler;

import com.efr.achievementbot.model.UserDB;
import com.efr.achievementbot.repository.user.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

// Заглушка для очистки месячного рейтинга

@Slf4j
@Component
@RequiredArgsConstructor
public class MonthlyResetScheduler {
    private final UserRepository userRepository;

    // Сброс месячного счёта в 00:00 1-го числа каждого месяца
    @Scheduled(cron = "0 0 0 1 * *")
    public void resetMonthlyScores() {
        List<UserDB> users = userRepository.findAll();
        users.forEach(user -> user.setMonthlyAchievementScore(0));
        userRepository.saveAll(users);
        log.info("Ежемесячные очки сброшены.");
    }
}
