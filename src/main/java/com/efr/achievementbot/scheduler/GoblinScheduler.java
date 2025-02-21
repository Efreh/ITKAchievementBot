package com.efr.achievementbot.scheduler;

import com.efr.achievementbot.config.bot.BotProperties;
import com.efr.achievementbot.service.goblin.GoblinService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.stereotype.Component;

import java.util.Calendar;
import java.util.Date;
import java.util.Random;
import java.util.concurrent.ScheduledFuture;

@Slf4j
@Component
@RequiredArgsConstructor
public class GoblinScheduler {

    private final BotProperties botProperties;
    private final GoblinService goblinService;
    private final ThreadPoolTaskScheduler taskScheduler;
    private final Random random = new Random();

    private ScheduledFuture<?> scheduledTask;

    @PostConstruct
    public void scheduleNextGoblin() {
        scheduleGoblinSpawn();
    }

//    private void scheduleGoblinSpawn() {
//        long now = System.currentTimeMillis();
//        // Случайный интервал: 2 или 3 дня
//        int daysInterval = random.nextBoolean() ? 2 : 3;
//        long intervalMillis = daysInterval * 24 * 60 * 60 * 1000L;
//        // Случайный час между 10 и 22
//        int randomHour = 10 + random.nextInt(13);
//        int randomMinute = random.nextInt(60);
//
//        Calendar calendar = Calendar.getInstance();
//        calendar.setTimeInMillis(now + intervalMillis);
//        calendar.set(Calendar.HOUR_OF_DAY, randomHour);
//        calendar.set(Calendar.MINUTE, randomMinute);
//        calendar.set(Calendar.SECOND, 0);
//        calendar.set(Calendar.MILLISECOND, 0);
//        Date nextSpawn = calendar.getTime();
//        log.info("Следующий спавн гоблина запланирован на: {}", nextSpawn);
//
//        scheduledTask = taskScheduler.schedule(() -> {
//            Long chatId = Long.parseLong(botProperties.getGroupId());
//            goblinService.spawnGoblin(chatId);
//            scheduleGoblinSpawn();
//        }, nextSpawn);
//    }


    // Альтернативный вариант для тестирования (каждые 30 секунд):
    // Раскомментируйте данный блок и закомментируйте основной метод scheduleNextGoblin(),
    // чтобы проверять работу бота в тестовом режиме.

    public void scheduleGoblinSpawn() {
        scheduledTask = taskScheduler.scheduleAtFixedRate(() -> {
            Long chatId = Long.parseLong(botProperties.getGroupId());
            goblinService.spawnGoblin(chatId);
            log.info("Спавн гоблина запущен по тестовому расписанию (30 секунд)");
        }, 60000); // 30 секунд = 30000 мс
    }
}
